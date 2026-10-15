package rs.ac.bg.fon.medicationregistry.dtos;

import jakarta.validation.constraints.Size;

public record CreateAdminDto(String firstName, String lastName, String username, @Size(min = 6) String password) {
}
