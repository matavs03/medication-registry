package rs.ac.bg.fon.medicationregistry.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateLetterRequest(@NotBlank String title, String description, @NotEmpty List<String> medicationsIds) {
}
