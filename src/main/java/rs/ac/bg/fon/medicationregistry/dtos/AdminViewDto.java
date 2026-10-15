package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.AdminRole;

public record AdminViewDto(String firstName, String lastName, String username, AdminRole role, boolean active) {
}
