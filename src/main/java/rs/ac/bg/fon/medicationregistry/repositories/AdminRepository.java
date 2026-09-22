package rs.ac.bg.fon.medicationregistry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.medicationregistry.domain.Admin;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUsername(String username);
}
