package rs.ac.bg.fon.medicationregistry.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LetterFullViewDto(UUID id, String title, String description, LocalDateTime createdAt, List<MedicationShortViewDto>  medications, AdminDto admin, StoredFileDto storedFile) {
}
