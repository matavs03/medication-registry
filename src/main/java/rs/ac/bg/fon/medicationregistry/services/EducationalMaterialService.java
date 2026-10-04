package rs.ac.bg.fon.medicationregistry.services;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.domain.EducationalMaterial;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.domain.StoredFile;
import rs.ac.bg.fon.medicationregistry.dtos.*;
import rs.ac.bg.fon.medicationregistry.exceptions.EducationalMaterialNotFoundException;
import rs.ac.bg.fon.medicationregistry.exceptions.FileStorageException;
import rs.ac.bg.fon.medicationregistry.exceptions.MedicationNotFoundException;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;
import rs.ac.bg.fon.medicationregistry.repositories.EducationalMaterialRepository;
import rs.ac.bg.fon.medicationregistry.repositories.MedicationRepository;
import rs.ac.bg.fon.medicationregistry.specifications.EducationalMaterialSpecifications;
import rs.ac.bg.fon.medicationregistry.storage.FileStorageService;

import java.util.*;

@Service
public class EducationalMaterialService {

    private final EducationalMaterialRepository educationalMaterialRepository;

    private final MedicationRepository medicationRepository;

    private final AdminRepository adminRepository;

    private final FileStorageService fileStorageService;

    public EducationalMaterialService(EducationalMaterialRepository educationalMaterialRepository, MedicationRepository medicationRepository, AdminRepository adminRepository, FileStorageService fileStorageService) {
        this.educationalMaterialRepository = educationalMaterialRepository;
        this.medicationRepository = medicationRepository;
        this.adminRepository = adminRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public EducationalMaterialFullViewDto createEducationalMaterial(CreateEducationalMaterialDto request, List<MultipartFile> files, String username) {
        if(files == null || files.isEmpty()) {
            throw new FileStorageException("There are no files to upload");
        }

        Set<Medication> medications = new HashSet<>(medicationRepository.findAllById(request.medicationsIds()));

        if(medications.size() != request.medicationsIds().size()) throw new MedicationNotFoundException("Medications not found");

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find admin with given username"));

        List<StoredFile> storedFiles = new ArrayList<>();

        for(MultipartFile file : files){
            StoredFile storedFile = fileStorageService.storeFile(file, FileStorageService.MATERIAL_TYPES);
            storedFiles.add(storedFile);
        }

        EducationalMaterial educationalMaterial = new EducationalMaterial();
        educationalMaterial.setTitle(request.title());
        educationalMaterial.setDescription(request.description());
        educationalMaterial.setMedications(medications);
        educationalMaterial.setAdmin(admin);
        educationalMaterial.setStoredFiles(storedFiles);

        educationalMaterialRepository.save(educationalMaterial);

        return convertToDto(educationalMaterial);
    }

    @Transactional
    public void deleteEducationalMaterial(UUID id) {
        EducationalMaterial educationalMaterial = educationalMaterialRepository.findById(id)
                .orElseThrow(() -> new EducationalMaterialNotFoundException("Educational material not found"));
        List<StoredFile> storedFiles = educationalMaterial.getStoredFiles();
        educationalMaterialRepository.delete(educationalMaterial);
        educationalMaterialRepository.flush();

        for(StoredFile storedFile : storedFiles){
            fileStorageService.deleteFile(storedFile);
        }
    }

    @Transactional(readOnly = true)
    public EducationalMaterialFullViewDto getEducationalMaterial(UUID id) {
        EducationalMaterial educationalMaterial = educationalMaterialRepository.findById(id)
                .orElseThrow(() -> new EducationalMaterialNotFoundException("Educational material not found"));
        return convertToDto(educationalMaterial);
    }

    @Transactional(readOnly = true)
    public Page<EducationalMaterialShortViewDto> findAll(int page, int size, EducationalMaterialSearchCriteria criteria) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());

        Specification<EducationalMaterial> spec = Specification.allOf(
                EducationalMaterialSpecifications.hasMedication(criteria.medicationId()),
                EducationalMaterialSpecifications.titleContains(criteria.title()),
                EducationalMaterialSpecifications.medicationNameContains(criteria.medicationName())
        );

        Page<EducationalMaterial> educationalMaterials = educationalMaterialRepository.findAll(spec, pageable);

        return educationalMaterials.map(em -> new EducationalMaterialShortViewDto(em.getId(), em.getTitle(), em.getCreatedAt()));
    }

    @Transactional(readOnly = true)
    public FileDownload downloadEducationalMaterialFile(UUID materialId, UUID fileId){
        EducationalMaterial educationalMaterial = educationalMaterialRepository.findById(materialId)
                .orElseThrow(() -> new EducationalMaterialNotFoundException("Educational material not found"));

        StoredFile storedFile = educationalMaterial.getStoredFiles().stream()
                .filter(f -> f.getId().equals(fileId))
                .findFirst().orElseThrow(() -> new FileStorageException("File does not belong to this Educational Material"));

        Resource resource = fileStorageService.loadFile(storedFile);
        return new FileDownload(storedFile.getOriginalFileName(), storedFile.getFileType(), resource);
    }


    private EducationalMaterialFullViewDto convertToDto(EducationalMaterial educationalMaterial) {
        List<MedicationShortViewDto> medicationsDto = new ArrayList<>();

        for(Medication m: educationalMaterial.getMedications()){
            medicationsDto.add(new MedicationShortViewDto(m.getId(), m.getName(), m.getInn(), m.getManufacturer()));
        }

        List<StoredFileDto> storedFiles = new ArrayList<>();

        for(StoredFile storedFile: educationalMaterial.getStoredFiles()){
            storedFiles.add(new StoredFileDto(storedFile.getId(), storedFile.getOriginalFileName(), storedFile.getFileType(), storedFile.getFileSize()));
        }

        return new EducationalMaterialFullViewDto(educationalMaterial.getId(), educationalMaterial.getTitle(), educationalMaterial.getDescription(), educationalMaterial.getCreatedAt(), medicationsDto, new AdminDto(educationalMaterial.getAdmin().getFirstName(), educationalMaterial.getAdmin().getLastName()), storedFiles);
    }
}
