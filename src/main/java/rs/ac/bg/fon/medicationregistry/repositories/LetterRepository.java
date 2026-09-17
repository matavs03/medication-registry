package rs.ac.bg.fon.medicationregistry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.bg.fon.medicationregistry.domain.Letter;

import java.util.UUID;

public interface LetterRepository extends JpaRepository<Letter, UUID> {
}
