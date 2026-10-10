package rs.ac.bg.fon.medicationregistry.specifications;

import org.springframework.data.jpa.domain.Specification;
import rs.ac.bg.fon.medicationregistry.domain.Letter;

import java.util.Locale;

public final class LetterSpecifications {

    private LetterSpecifications() {
    }

    public static Specification<Letter> titleContains(String title) {
        return (root, query, cb) -> title == null || title.isBlank() ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }


    public static Specification<Letter> medicationNameContains(String medicationName){
        return (root, query, cb) -> medicationName == null || medicationName.isBlank() ? null
                : cb.like(cb.lower(root.join("medications").get("name")),"%" +  medicationName.toLowerCase() + "%");
    }

}
