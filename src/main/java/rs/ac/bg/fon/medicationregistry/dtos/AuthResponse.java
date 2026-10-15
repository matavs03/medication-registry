package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.AdminRole;

import java.time.Instant;

public record AuthResponse(String token, String username, String firstName, String lastName, AdminRole role, Instant expiresAt) {
}
