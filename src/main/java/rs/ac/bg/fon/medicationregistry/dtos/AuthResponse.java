package rs.ac.bg.fon.medicationregistry.dtos;

import java.time.Instant;

public record AuthResponse(String token, String username, String firstName, String lastName, Instant expiresAt) {
}
