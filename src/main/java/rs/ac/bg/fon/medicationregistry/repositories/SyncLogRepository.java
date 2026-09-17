package rs.ac.bg.fon.medicationregistry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.medicationregistry.domain.SyncLog;

import java.util.UUID;

public interface SyncLogRepository extends JpaRepository<SyncLog, UUID> {
}
