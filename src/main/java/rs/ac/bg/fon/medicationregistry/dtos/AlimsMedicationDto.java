package rs.ac.bg.fon.medicationregistry.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlimsMedicationDto(String sifraProizvoda,
                                 String inn,
                                 String oblikIDozaLeka,
                                 String proizvodjac,
                                 String nazivLeka,
                                 String atc,
                                 String vrstaLeka,
                                 String rezimIzdavanjaLeka,
                                 LocalDate datumVazenjaResenja
                                 ){}
