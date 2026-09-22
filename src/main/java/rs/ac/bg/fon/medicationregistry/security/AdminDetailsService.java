package rs.ac.bg.fon.medicationregistry.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminDetailsService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Administrator nije pronađen"));

        return User.withUsername(admin.getUsername())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();
    }
}
