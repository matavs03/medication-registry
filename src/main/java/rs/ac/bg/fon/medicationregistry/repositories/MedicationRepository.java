package rs.ac.bg.fon.medicationregistry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rs.ac.bg.fon.medicationregistry.domain.Medication;

public interface MedicationRepository extends JpaRepository<Medication, String>,
        JpaSpecificationExecutor<Medication> {
}
