package rs.ac.bg.fon.medicationregistry.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.domain.MedicationStatus;
import rs.ac.bg.fon.medicationregistry.domain.SyncLog;
import rs.ac.bg.fon.medicationregistry.domain.SyncStatus;
import rs.ac.bg.fon.medicationregistry.dtos.AlimsMedicationDto;
import rs.ac.bg.fon.medicationregistry.dtos.AlimsResponseDto;
import rs.ac.bg.fon.medicationregistry.exceptions.AlimsUnavailableException;
import rs.ac.bg.fon.medicationregistry.integration.AlimsClient;
import rs.ac.bg.fon.medicationregistry.repositories.MedicationRepository;
import rs.ac.bg.fon.medicationregistry.repositories.SyncLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationSyncServiceTest {

    @Mock
    private AlimsClient alimsClient;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private SyncLogRepository syncLogRepository;

    @Captor
    private ArgumentCaptor<List<Medication>> medicationsCaptor;

    @Captor
    private ArgumentCaptor<SyncLog> syncLogCaptor;

    private MedicationSyncService service;

    @BeforeEach
    void setUp() {
        service = new MedicationSyncService(alimsClient, medicationRepository,
                syncLogRepository, 0.8, 3);
    }

    private AlimsMedicationDto dto(String id, String name, String manufacturer) {
        return new AlimsMedicationDto(id, "inn", "oblik", manufacturer, name,
                "A01AA01", "Humani lekovi", "R", LocalDate.of(2030, 1, 1));
    }

    @Test
    void addingNewMedication() {
        AlimsResponseDto response = new AlimsResponseDto("pregled", List.of(dto("1", "Brufen", "Abbott")));

        when(alimsClient.fetchMedications()).thenReturn(response);
        when(medicationRepository.findAll()).thenReturn(List.of());

        service.syncMedication();

        verify(medicationRepository).saveAll(medicationsCaptor.capture());

        List<Medication> saved = medicationsCaptor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getId()).isEqualTo("1");
        assertThat(saved.get(0).getName()).isEqualTo("Brufen");
        assertThat(saved.get(0).getStatus()).isEqualTo(MedicationStatus.ACTIVE);
        assertThat(saved.get(0).getFirstSeen()).isNotNull();
    }

    @Test
    void updatingChangedMedication() {
        Medication postojeci = new Medication();
        postojeci.setId("1");
        postojeci.setName("Brufen");
        postojeci.setManufacturer("SANDOZ");
        postojeci.setStatus(MedicationStatus.ACTIVE);
        postojeci.setFirstSeen(LocalDateTime.now().minusDays(100));
        postojeci.setLastSeen(LocalDateTime.now().minusDays(1));

        when(alimsClient.fetchMedications())
                .thenReturn(new AlimsResponseDto("pregled", List.of(dto("1", "Brufen", "NOVARTIS"))));
        when(medicationRepository.findAll()).thenReturn(List.of(postojeci));

        service.syncMedication();

        verify(syncLogRepository).save(syncLogCaptor.capture());
        assertThat(syncLogCaptor.getValue().getChangedCount()).isEqualTo(1);
        assertThat(postojeci.getManufacturer()).isEqualTo("NOVARTIS");
    }

    @Test
    void nonChangedMedicationIsNotCountedAsAChange() {
        Medication postojeci = new Medication();
        postojeci.setId("1");
        postojeci.setName("Brufen");
        postojeci.setInn("inn");
        postojeci.setDosageForm("oblik");
        postojeci.setManufacturer("Abbott");
        postojeci.setAtc("A01AA01");
        postojeci.setType("Humani lekovi");
        postojeci.setPrescriptionMode("R");
        postojeci.setLicenseValidUntil(LocalDate.of(2030, 1, 1));
        postojeci.setStatus(MedicationStatus.ACTIVE);
        postojeci.setFirstSeen(LocalDateTime.now().minusDays(100));
        postojeci.setLastSeen(LocalDateTime.now().minusDays(1));

        when(alimsClient.fetchMedications())
                .thenReturn(new AlimsResponseDto("pregled", List.of(dto("1", "Brufen", "Abbott"))));
        when(medicationRepository.findAll()).thenReturn(List.of(postojeci));

        service.syncMedication();

        verify(syncLogRepository).save(syncLogCaptor.capture());
        assertThat(syncLogCaptor.getValue().getChangedCount()).isZero();
    }

    @Test
    void withdrawingMedicationThatDidntArrive() {
        Medication stari = new Medication();
        stari.setId("2");
        stari.setName("Stari lek");
        stari.setStatus(MedicationStatus.ACTIVE);
        stari.setFirstSeen(LocalDateTime.now().minusDays(100));
        stari.setLastSeen(LocalDateTime.now().minusDays(10));

        when(alimsClient.fetchMedications())
                .thenReturn(new AlimsResponseDto("pregled", List.of(dto("1", "Novi", "Abbott"))));
        when(medicationRepository.findAll()).thenReturn(List.of(stari));

        service.syncMedication();

        assertThat(stari.getStatus()).isEqualTo(MedicationStatus.WITHDRAWN);
    }

    @Test
    void takingFirstMedicationFromDuplicates() {
        when(alimsClient.fetchMedications()).thenReturn(new AlimsResponseDto("pregled",
                List.of(dto("1", "Exjade", "NOVARTIS"), dto("1", "Exjade", "SANDOZ"))));
        when(medicationRepository.findAll()).thenReturn(List.of());

        service.syncMedication();

        verify(medicationRepository).saveAll(medicationsCaptor.capture());

        List<Medication> saved = medicationsCaptor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getManufacturer()).isEqualTo("NOVARTIS");

        verify(syncLogRepository).save(syncLogCaptor.capture());
        assertThat(syncLogCaptor.getValue().getReceivedCount()).isEqualTo(1);
    }

    @Test
    void RejectingTooFewMedications() {
        List<Medication> postojeci = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Medication m = new Medication();
            m.setId(String.valueOf(i));
            m.setStatus(MedicationStatus.ACTIVE);
            m.setLastSeen(LocalDateTime.now());
            postojeci.add(m);
        }

        when(alimsClient.fetchMedications())
                .thenReturn(new AlimsResponseDto("pregled", List.of(dto("1", "Jedini", "Abbott"))));
        when(medicationRepository.findAll()).thenReturn(postojeci);

        service.syncMedication();

        verify(medicationRepository, never()).saveAll(anyList());

        verify(syncLogRepository).save(syncLogCaptor.capture());
        assertThat(syncLogCaptor.getValue().getStatus()).isEqualTo(SyncStatus.REJECTED);
    }

    @Test
    void unavailableAlimsIsFinishedAsFailed() {
        when(alimsClient.fetchMedications())
                .thenThrow(new AlimsUnavailableException("ALIMS nije dostupan"));

        service.syncMedication();

        verify(medicationRepository, never()).saveAll(anyList());

        verify(syncLogRepository).save(syncLogCaptor.capture());
        assertThat(syncLogCaptor.getValue().getStatus()).isEqualTo(SyncStatus.FAILED);
    }
}