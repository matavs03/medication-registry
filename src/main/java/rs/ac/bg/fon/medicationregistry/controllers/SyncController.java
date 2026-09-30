package rs.ac.bg.fon.medicationregistry.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.medicationregistry.dtos.SyncLogDto;
import rs.ac.bg.fon.medicationregistry.services.MedicationSyncService;

@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private final MedicationSyncService medicationSyncService;

    public SyncController(MedicationSyncService medicationSyncService) {
        this.medicationSyncService = medicationSyncService;
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<SyncLogDto>> findAll(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "50") int size){
        return ResponseEntity.ok(medicationSyncService.findAll(page, size));
    }

    @PostMapping
    public ResponseEntity<SyncLogDto> manualSync(){

        return ResponseEntity.ok(medicationSyncService.syncMedication());
    }
}
