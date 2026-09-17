package rs.ac.bg.fon.medicationregistry.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlimsResponseDto(
        String datumIVremeKreiranjaPregleda,
        List<AlimsMedicationDto> lekovi
) {}
