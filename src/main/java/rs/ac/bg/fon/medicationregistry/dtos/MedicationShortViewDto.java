package rs.ac.bg.fon.medicationregistry.dtos;

public record MedicationShortViewDto(
        String id,
        String name,
        String inn,
        String manufacturer
) {
}
