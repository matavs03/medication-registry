package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.MedicationStatus;

import java.time.LocalDate;

public record MedicationFullViewDto(
        String id,
        String inn,
        String dosageForm,
        String manufacturer,
        String name,
        String atc,
        String type,
        String prescriptionMode,
        LocalDate licenseValidUntil,
        MedicationStatus status
) {
}
