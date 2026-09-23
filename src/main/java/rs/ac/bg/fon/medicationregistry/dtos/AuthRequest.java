package rs.ac.bg.fon.medicationregistry.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String username,@NotBlank String password) {
}
