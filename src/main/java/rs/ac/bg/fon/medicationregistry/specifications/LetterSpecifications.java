package rs.ac.bg.fon.medicationregistry.specifications;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.bg.fon.medicationregistry.domain.Letter;

public final class LetterSpecifications {

    public static Specification<Letter> titleContains(String title) {
        return (root, query, cb) -> title == null || title.isBlank() ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Letter> hasMedication(String medicationId){
        return (root, query, cb) -> medicationId == null || medicationId.isBlank() ? null
                : cb.equal(root.join("medications").get("id"), medicationId);
    }

}
