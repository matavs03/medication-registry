package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.AdminRole;

import java.util.UUID;

public record AdminViewDto(UUID id, String firstName, String lastName, String username, AdminRole role, boolean active) {
}
