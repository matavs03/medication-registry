package rs.ac.bg.fon.medicationregistry.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MedicationSyncService {

    private final AlimsClient alimsClient;

    private final MedicationRepository medicationRepository;

    private final SyncLogRepository syncLogRepository;

    private final double error;

    private final int daysMargin;

    public MedicationSyncService(AlimsClient alimsClient, MedicationRepository medicationRepository, SyncLogRepository syncLogRepository, @Value("${medication.error}") double error, @Value("${medication.days.margin}") int  daysMargin) {
        this.alimsClient = alimsClient;
        this.medicationRepository = medicationRepository;
        this.syncLogRepository = syncLogRepository;
        this.error = error;
        this.daysMargin = daysMargin;
    }

    @Scheduled(cron = "${alims.sync-cron}")
    @Transactional
    public void syncMedication(){
        SyncLog syncLog = new SyncLog();

        try {
            AlimsResponseDto response = alimsClient.fetchMedications();
            List<AlimsMedicationDto> dtos = response.lekovi();

            Map<String, AlimsMedicationDto> unique = dtos.stream()
                    .collect(Collectors.toMap(
                            AlimsMedicationDto::sifraProizvoda,
                            dto -> dto,
                            (first, second) -> first
                    ));

            int dtosSize = unique.size();



            syncLog.setReceivedCount(dtosSize);

            Map<String, Medication> currentMedications = medicationRepository.findAll().stream()
                    .collect(Collectors.toMap(Medication::getId, medication -> medication));

            long currentCount = currentMedications.size();

            if(dtosSize < error * currentCount){
                syncLog.setStatus(SyncStatus.REJECTED);
                syncLog.setMessage("Received count was lower than allowed");
                syncLogRepository.save(syncLog);
                return;
            }


            LocalDateTime now = LocalDateTime.now();
            List<Medication> forDB = new ArrayList<>();
            int changedCount = 0;


            for(AlimsMedicationDto dto : unique.values()){
                Medication medication = currentMedications.remove(dto.sifraProizvoda());
                if(medication == null){
                    medication = new Medication();
                    medication.setId(dto.sifraProizvoda());
                    medication.setFirstSeen(now);
                    applyData(medication, dto);
                    medication.setStatus(MedicationStatus.ACTIVE);
                    changedCount++;
                } else if(hasChanges(medication, dto) || medication.getStatus() == MedicationStatus.WITHDRAWN){
                    applyData(medication, dto);
                    medication.setStatus(MedicationStatus.ACTIVE);
                    changedCount++;
                }

                medication.setLastSeen(now);
                forDB.add(medication);
            }

            LocalDateTime margin = now.minusDays(daysMargin);

            for(Medication med: currentMedications.values()){
                if(med.getLastSeen().isBefore(margin) && med.getStatus() == MedicationStatus.ACTIVE){
                    med.setStatus(MedicationStatus.WITHDRAWN);
                    changedCount++;
                    forDB.add(med);
                }
            }

            medicationRepository.saveAll(forDB);

            syncLog.setMessage(response.datumIVremeKreiranjaPregleda());
            syncLog.setChangedCount(changedCount);
            syncLog.setStatus(SyncStatus.SUCCESS);

        } catch (Exception e){
            log.error("Sync error", e);
            syncLog.setStatus(SyncStatus.FAILED);
            syncLog.setMessage(e.getMessage());
        }

        syncLogRepository.save(syncLog);
    }


    private static String clean(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return (trimmed.isEmpty() || trimmed.equals("/")) ? null : trimmed;
    }

    private void applyData(Medication medication, AlimsMedicationDto dto) {
        medication.setName(clean(dto.nazivLeka()));
        medication.setInn(clean(dto.inn()));
        medication.setDosageForm(clean(dto.oblikIDozaLeka()));
        medication.setManufacturer(clean(dto.proizvodjac()));
        medication.setAtc(clean(dto.atc()));
        medication.setType(clean(dto.vrstaLeka()));
        medication.setPrescriptionMode(clean(dto.rezimIzdavanjaLeka()));
        medication.setLicenseValidUntil(dto.datumVazenjaResenja());
    }

    private boolean hasChanges(Medication m, AlimsMedicationDto dto) {
        return !Objects.equals(m.getName(), clean(dto.nazivLeka()))
                || !Objects.equals(m.getInn(), clean(dto.inn()))
                || !Objects.equals(m.getDosageForm(), clean(dto.oblikIDozaLeka()))
                || !Objects.equals(m.getManufacturer(), clean(dto.proizvodjac()))
                || !Objects.equals(m.getAtc(), clean(dto.atc()))
                || !Objects.equals(m.getType(), clean(dto.vrstaLeka()))
                || !Objects.equals(m.getPrescriptionMode(), clean(dto.rezimIzdavanjaLeka()))
                || !Objects.equals(m.getLicenseValidUntil(), dto.datumVazenjaResenja());
    }

}
