package rs.ac.bg.fon.medicationregistry.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rs.ac.bg.fon.medicationregistry.domain.EducationalMaterial;

import java.util.UUID;

public interface EducationalMaterialRepository extends JpaRepository<EducationalMaterial, UUID>,
        JpaSpecificationExecutor<EducationalMaterial> {
}
