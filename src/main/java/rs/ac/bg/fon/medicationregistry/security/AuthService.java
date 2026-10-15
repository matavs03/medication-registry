package rs.ac.bg.fon.medicationregistry.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.bg.fon.medicationregistry.domain.Admin;
import rs.ac.bg.fon.medicationregistry.dtos.AuthRequest;
import rs.ac.bg.fon.medicationregistry.dtos.AuthResponse;
import rs.ac.bg.fon.medicationregistry.exceptions.NonValidPasswordException;
import rs.ac.bg.fon.medicationregistry.repositories.AdminRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AdminRepository adminRepository;
    private final long tokenHours;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, AdminRepository adminRepository, @Value("${security.jwt.ttl-hours}") long tokenHours, JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.tokenHours = tokenHours;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        Admin admin = adminRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new UsernameNotFoundException("Administator nije pronadjen"));

        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenHours, ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("medication-registry")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(admin.getUsername())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new AuthResponse(token, admin.getUsername(), admin.getFirstName(), admin.getLastName(), expiresAt);
    }

    @Transactional
    public void changeOwnPassword(String username, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Administrator nije pronadjen"));

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new BadCredentialsException("Stara lozinka nije ispravna");
        }

        if(newPassword == null || newPassword.length() < 6) {
            throw new NonValidPasswordException("Lozinka mora imati najmanje 6 karaktera");
        }


        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
    }
}
