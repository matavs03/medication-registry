package rs.ac.bg.fon.medicationregistry.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationFullViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationSearchCriteria;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationShortViewDto;
import rs.ac.bg.fon.medicationregistry.exceptions.MedicationNotFoundException;
import rs.ac.bg.fon.medicationregistry.repositories.MedicationRepository;
import rs.ac.bg.fon.medicationregistry.specifications.MedicationSpecifications;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Transactional(readOnly = true)
    public Page<MedicationShortViewDto> findAll(MedicationSearchCriteria criteria, int page, int size){

        int safeSize = Math.clamp(size, 1, 100);
        int safePage = Math.max(0, page);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("name"));

        Specification<Medication> spec = Specification.allOf(
                MedicationSpecifications.nameContains(criteria.name()),
                MedicationSpecifications.innContains(criteria.inn()),
                MedicationSpecifications.atcStartsWith(criteria.atc()),
                MedicationSpecifications.hasStatus(criteria.status())
        );

        Page<Medication> medicationPage = medicationRepository.findAll(spec, pageable);

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
