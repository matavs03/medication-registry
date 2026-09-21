package rs.ac.bg.fon.medicationregistry.specifications;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.bg.fon.medicationregistry.domain.Medication;
import rs.ac.bg.fon.medicationregistry.domain.MedicationStatus;

public final class MedicationSpecifications {
    private MedicationSpecifications() {}

    public static Specification<Medication> nameContains(String name){
        return (root, query, cb) -> name==null || name.isBlank() ? null
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Medication> atcStartsWith(String atc){
        return (root, query, cb) -> atc ==null || atc.isBlank() ? null
                : cb.like(root.get("atc"), atc.toUpperCase() + "%");
    }

    public static Specification<Medication> hasStatus(MedicationStatus status) {
        return (root, query, cb) -> status == null ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Medication> innContains(String inn){
        return (root, query, cb) -> inn==null || inn.isBlank() ? null
                : cb.like(cb.lower(root.get("name")), "%" + inn.toLowerCase() + "%");
    }
}
