package rs.ac.bg.fon.medicationregistry.dtos;

import rs.ac.bg.fon.medicationregistry.domain.MedicationStatus;

public record MedicationSearchCriteria(String name, String inn, String atc, MedicationStatus status) {
}
