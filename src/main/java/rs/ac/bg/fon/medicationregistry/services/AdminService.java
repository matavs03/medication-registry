package rs.ac.bg.fon.medicationregistry.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.domain.AdminRole;
import rs.ac.bg.fon.medicationregistry.dtos.AdminViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.CreateAdminDto;
import rs.ac.bg.fon.medicationregistry.exceptions.*;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminViewDto createAdmin(CreateAdminDto request) {
        Optional<Admin> current = adminRepository.findByUsername(request.username());
        if(current.isPresent()) {
            throw new UsernameAlreadyExistsException("Username vec postoji");
        }

        if(request.password() == null || request.password().length() < 6) {
            throw new NonValidPasswordException("Lozinka mora imati najmanje 6 karaktera");
        }

        Admin admin = new Admin();
        admin.setFirstName(request.firstName());
        admin.setLastName(request.lastName());
        admin.setUsername(request.username());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setRole(AdminRole.ADMIN);
        admin.setActive(true);

        adminRepository.save(admin);

        return convertToDto(admin);
    }

    @Transactional(readOnly = true)
    public Page<AdminViewDto> getAll(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.clamp(size, 1, 100);

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "username"));

        Page<Admin> admins = adminRepository.findAll(pageable);

        return admins.map(this::convertToDto);
    }

    @Transactional
    public AdminViewDto deactivateAdmin(UUID deactivateId, String requestUsername) {
        Admin admin = adminRepository.findById(deactivateId)
                .orElseThrow(() -> new AdminNotFoundException("Admin nije pronadjen"));

        if(admin.getUsername().equals(requestUsername)) {
            throw new NonValidAdminDeactivationException("Ne mozete deaktivirati svoj nalog");
        }

        if(!admin.isActive()) {
            throw  new NonValidAdminDeactivationException("Nalog je vec deaktiviran");
        }

        admin.setActive(false);
        adminRepository.save(admin);

        return convertToDto(admin);
    }

    @Transactional
    public AdminViewDto activateAdmin(UUID activateId) {
        Admin admin = adminRepository.findById(activateId)
                .orElseThrow(() -> new AdminNotFoundException("Admin nije pronadjen"));

        if(admin.isActive()) {
            throw  new NonValidAdminActivationException("Nalog je vec aktiviran");
        }

        admin.setActive(true);
        adminRepository.save(admin);

        return convertToDto(admin);
    }

    @Transactional
    public void changePassword(UUID id, String newPassword){
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException("Admin nije pronadjen"));

        if(newPassword == null || newPassword.length() < 6) {
            throw new NonValidPasswordException("Lozinka mora imati najmanje 6 karaktera");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));

        adminRepository.save(admin);
    }

    private AdminViewDto convertToDto(Admin admin) {
        return new AdminViewDto(admin.getId(), admin.getFirstName(), admin.getLastName(), admin.getUsername(), admin.getRole(), admin.isActive());
    }
}
