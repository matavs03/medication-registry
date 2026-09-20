package rs.ac.bg.fon.medicationregistry.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationFullViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationShortViewDto;
import rs.ac.bg.fon.medicationregistry.exceptions.MedicationNotFoundException;
import rs.ac.bg.fon.medicationregistry.repositories.MedicationRepository;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    public Page<MedicationShortViewDto> findAll(int page, int size){

        int safeSize = Math.clamp(size, 1, 100);
        int safePage = Math.max(0, page);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("name"));

        Page<Medication> medicationPage = medicationRepository.findAll(pageable);

        return medicationPage.map(m -> new MedicationShortViewDto(
                m.getId(),
                m.getName(),
                m.getInn(),
                m.getManufacturer()
        ));
    }

    public MedicationFullViewDto findById(String id){

        Medication m = medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundException("Medication with id " + id + " not found"));

        return new MedicationFullViewDto(
                m.getId(),
                m.getInn(),
                m.getDosageForm(),
                m.getManufacturer(),
                m.getName(),
                m.getAtc(),
                m.getType(),
                m.getPrescriptionMode(),
                m.getLicenseValidUntil(),
                m.getStatus()
        );
    }
}
