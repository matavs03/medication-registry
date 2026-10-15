package rs.ac.bg.fon.medicationregistry.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.medicationregistry.dtos.AdminViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.CreateAdminDto;
import rs.ac.bg.fon.medicationregistry.dtos.ResetPasswordDto;
import rs.ac.bg.fon.medicationregistry.services.AdminService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admins")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<Page<AdminViewDto>> findAll(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAll(page, size));
    }

    @PostMapping
    public ResponseEntity<AdminViewDto> createAdmin(@Valid @RequestBody CreateAdminDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(request));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AdminViewDto> deactivateAdmin(@PathVariable UUID id,
                                                        Authentication authentication) {
        return ResponseEntity.ok(adminService.deactivateAdmin(id, authentication.getName()));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AdminViewDto> activateAdmin(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.activateAdmin(id));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id,
                                                      @Valid @RequestBody ResetPasswordDto request) {
        adminService.resetPassword(id, request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
