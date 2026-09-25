package rs.ac.bg.fon.medicationregistry.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.domain.Letter;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.domain.StoredFile;
import rs.ac.bg.fon.medicationregistry.dtos.*;
import rs.ac.bg.fon.medicationregistry.exceptions.MedicationNotFoundException;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;
import rs.ac.bg.fon.medicationregistry.repositories.LetterRepository;
import rs.ac.bg.fon.medicationregistry.repositories.MedicationRepository;
import rs.ac.bg.fon.medicationregistry.storage.FileStorageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LetterService {

    private final LetterRepository letterRepository;
    private final MedicationRepository medicationRepository;
    private final AdminRepository adminRepository;
    private final FileStorageService  fileStorageService;

    public LetterService(LetterRepository letterRepository, MedicationRepository medicationRepository, AdminRepository adminRepository, FileStorageService fileStorageService) {
        this.letterRepository = letterRepository;
        this.medicationRepository = medicationRepository;
        this.adminRepository = adminRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public LetterFullViewDto createLetter(CreateLetterRequest request, MultipartFile file, String username) {

        Set<Medication> medications = new HashSet<>(medicationRepository.findAllById(request.medicationsIds()));

        if(medications.size() != request.medicationsIds().size()) throw new MedicationNotFoundException("Medications not found");

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find admin with given username"));

        StoredFile storedFile = fileStorageService.storeFile(file);

        Letter letter = new  Letter();
        letter.setMedications(medications);
        letter.setDescription(request.description());
        letter.setTitle(request.title());
        letter.setAdmin(admin);
        letter.setStoredFile(storedFile);

        letterRepository.save(letter);

        return convertToDto(letter);
    }

    private LetterFullViewDto convertToDto(Letter letter){
        List<MedicationShortViewDto> medicationsDto = new ArrayList<>();

        for(Medication m: letter.getMedications()){
            medicationsDto.add(new MedicationShortViewDto(m.getId(), m.getName(), m.getInn(), m.getManufacturer()));
        }

        return new LetterFullViewDto(letter.getId(), letter.getTitle(), letter.getDescription(), letter.getCreatedAt(), medicationsDto, new AdminDto(letter.getAdmin().getFirstName(), letter.getAdmin().getLastName()), new StoredFileDto(letter.getStoredFile().getId(), letter.getStoredFile().getOriginalFileName(), letter.getStoredFile().getFileType(), letter.getStoredFile().getFileSize()));
    }
}
