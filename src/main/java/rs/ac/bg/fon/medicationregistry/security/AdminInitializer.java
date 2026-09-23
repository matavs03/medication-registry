package rs.ac.bg.fon.medicationregistry.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;

@Component
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public AdminInitializer(AdminRepository adminRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${admin.default.username}") String username,
                            @Value("${admin.default.password}") String password) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.findByUsername(username).isPresent()) {
            return;
        }
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setFirstName("Podrazumevani");
        admin.setLastName("Administrator");
        adminRepository.save(admin);
        log.info("Kreiran pocetni administrator: {}", username);

    }
}
