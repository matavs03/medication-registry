package rs.ac.bg.fon.medicationregistry.specifications;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.bg.fon.medicationregistry.domain.EducationalMaterial;
import rs.ac.bg.fon.medicationregistry.domain.Letter;

public final class EducationalMaterialSpecifications {

    private EducationalMaterialSpecifications() {
    }

    public static Specification<EducationalMaterial> titleContains(String title) {
        return (root, query, cb) -> title == null || title.isBlank() ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }


    public static Specification<EducationalMaterial> medicationNameContains(String medicationName){
        return (root, query, cb) -> medicationName == null || medicationName.isBlank() ? null
                : cb.like(cb.lower(root.join("medications").get("name")),"%" +  medicationName.toLowerCase() + "%");
    }

}
