package rs.ac.bg.fon.medicationregistry.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record LetterShortViewDto(UUID id, String title, LocalDateTime createdAt) {
}
