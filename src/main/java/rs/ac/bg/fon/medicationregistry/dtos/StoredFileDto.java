package rs.ac.bg.fon.medicationregistry.dtos;

import java.util.UUID;

public record StoredFileDto(UUID id, String originalFileName, String fileType, Long fileSize) {
}
