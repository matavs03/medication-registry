package rs.ac.bg.fon.medicationregistry.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.domain.AdminRole;
import rs.ac.bg.fon.medicationregistry.dtos.AdminViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.CreateAdminDto;
import rs.ac.bg.fon.medicationregistry.exceptions.AdminNotFoundException;
import rs.ac.bg.fon.medicationregistry.exceptions.NonValidAdminDeactivationException;
import rs.ac.bg.fon.medicationregistry.exceptions.UsernameAlreadyExistsException;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @Captor
    private ArgumentCaptor<Admin> adminCaptor;

    private Admin admin(String username, boolean active) {
        Admin admin = new Admin();
        admin.setId(UUID.randomUUID());
        admin.setFirstName("Petar");
        admin.setLastName("Petrovic");
        admin.setUsername(username);
        admin.setPassword("hash");
        admin.setRole(AdminRole.ADMIN);
        admin.setActive(active);
        return admin;
    }

    @Test
    void creatingAdminHashesPasswordAndSetsAdminRole() {
        CreateAdminDto request = new CreateAdminDto("Petar", "Petrovic", "pera", "lozinka123");

        when(adminRepository.findByUsername("pera")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("lozinka123")).thenReturn("hashirana");

        AdminViewDto result = adminService.createAdmin(request);

        verify(adminRepository).save(adminCaptor.capture());

        Admin saved = adminCaptor.getValue();
        assertThat(saved.getUsername()).isEqualTo("pera");
        assertThat(saved.getPassword()).isEqualTo("hashirana");
        assertThat(saved.getRole()).isEqualTo(AdminRole.ADMIN);
        assertThat(saved.isActive()).isTrue();

        assertThat(result.username()).isEqualTo("pera");
    }

    @Test
    void creatingAdminWithExistingUsernameFails() {
        CreateAdminDto request = new CreateAdminDto("Petar", "Petrovic", "pera", "lozinka123");

        when(adminRepository.findByUsername("pera")).thenReturn(Optional.of(admin("pera", true)));

        assertThatThrownBy(() -> adminService.createAdmin(request))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(adminRepository, never()).save(any());
    }

    @Test
    void deactivatingOwnAccountFails() {
        Admin pera = admin("pera", true);

        when(adminRepository.findById(pera.getId())).thenReturn(Optional.of(pera));

        assertThatThrownBy(() -> adminService.deactivateAdmin(pera.getId(), "pera"))
                .isInstanceOf(NonValidAdminDeactivationException.class);

        assertThat(pera.isActive()).isTrue();
        verify(adminRepository, never()).save(any());
    }

    @Test
    void deactivatingAlreadyDeactivatedAccountFails() {
        Admin pera = admin("pera", false);

        when(adminRepository.findById(pera.getId())).thenReturn(Optional.of(pera));

        assertThatThrownBy(() -> adminService.deactivateAdmin(pera.getId(), "glavni"))
                .isInstanceOf(NonValidAdminDeactivationException.class);

        verify(adminRepository, never()).save(any());
    }

    @Test
    void deactivatingAnotherAdminSucceeds() {
        Admin pera = admin("pera", true);

        when(adminRepository.findById(pera.getId())).thenReturn(Optional.of(pera));

        AdminViewDto result = adminService.deactivateAdmin(pera.getId(), "glavni");

        assertThat(pera.isActive()).isFalse();
        assertThat(result.active()).isFalse();
        verify(adminRepository).save(pera);
    }

    @Test
    void activatingDeactivatedAdminSucceeds() {
        Admin pera = admin("pera", false);

        when(adminRepository.findById(pera.getId())).thenReturn(Optional.of(pera));

        AdminViewDto result = adminService.activateAdmin(pera.getId());

        assertThat(pera.isActive()).isTrue();
        assertThat(result.active()).isTrue();
        verify(adminRepository).save(pera);
    }

    @Test
    void operationOnNonExistingAdminFails() {
        UUID id = UUID.randomUUID();

        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.deactivateAdmin(id, "glavni"))
                .isInstanceOf(AdminNotFoundException.class);
    }
}