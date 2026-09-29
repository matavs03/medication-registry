package rs.ac.bg.fon.medicationregistry.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record EducationalMaterialShortViewDto(UUID id, String title, LocalDateTime createdAt) {
}
