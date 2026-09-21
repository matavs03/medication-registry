package rs.ac.bg.fon.medicationregistry.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationFullViewDto;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationSearchCriteria;
import rs.ac.bg.fon.medicationregistry.dtos.MedicationShortViewDto;
import rs.ac.bg.fon.medicationregistry.services.MedicationService;

@RestController
@RequestMapping("/api/v1/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping()
    public ResponseEntity<Page<MedicationShortViewDto>> findAll(MedicationSearchCriteria criteria,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "50") int size){
        return ResponseEntity.ok(medicationService.findAll(criteria, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationFullViewDto> findById(@PathVariable String id){
        return ResponseEntity.ok(medicationService.findById(id));
    }
}
