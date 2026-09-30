package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.SyncStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record SyncLogDto(UUID id, LocalDateTime syncDateTime, int receivedCount, int changedCount, SyncStatus syncStatus, String message) {
}
