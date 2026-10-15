package rs.ac.bg.fon.medicationregistry.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.medicationregistry.dtos.AuthRequest;
import rs.ac.bg.fon.medicationregistry.dtos.AuthResponse;
import rs.ac.bg.fon.medicationregistry.dtos.ChangePasswordDto;
import rs.ac.bg.fon.medicationregistry.security.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changeOwnPassword(@Valid @RequestBody ChangePasswordDto request,
                                                  Authentication authentication) {
        authService.changeOwnPassword(authentication.getName(), request.oldPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

}
