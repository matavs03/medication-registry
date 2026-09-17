package rs.ac.bg.fon.medicationregistry.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.bg.fon.medicationregistry.dtos.AlimsMedicationDto;
import rs.ac.bg.fon.medicationregistry.integration.AlimsClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class TestController {
    private final AlimsClient alimsClient;

    public TestController(AlimsClient alimsClient) {
        this.alimsClient = alimsClient;
    }

    @GetMapping("/test/alims")
    public String test() {
        List<AlimsMedicationDto> lekovi = alimsClient.fetchMedications();
        return "Broj lekova: " + lekovi.size() + ", prvi: " + lekovi.get(0);
    }

    @GetMapping("/test/vrednosti")
    public Map<String, Object> vrednosti() {
        List<AlimsMedicationDto> lekovi = alimsClient.fetchMedications();

        Map<String, Long> vrste = lekovi.stream()
                .collect(Collectors.groupingBy(
                        l -> l.vrstaLeka() == null ? "(prazno)" : l.vrstaLeka(),
                        Collectors.counting()));

        Map<String, Long> rezimi = lekovi.stream()
                .collect(Collectors.groupingBy(
                        l -> l.rezimIzdavanjaLeka() == null ? "(prazno)" : l.rezimIzdavanjaLeka(),
                        Collectors.counting()));

        return Map.of("vrstaLeka", vrste, "rezimIzdavanjaLeka", rezimi);
    }
}
