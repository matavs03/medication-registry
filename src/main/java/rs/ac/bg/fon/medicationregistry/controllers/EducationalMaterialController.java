package rs.ac.bg.fon.medicationregistry.controllers;


import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.dtos.*;
import rs.ac.bg.fon.medicationregistry.services.EducationalMaterialService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/materials")
public class EducationalMaterialController {

    private final EducationalMaterialService educationalMaterialService;

    public EducationalMaterialController(EducationalMaterialService educationalMaterialService) {
        this.educationalMaterialService = educationalMaterialService;
    }

    @GetMapping
    public ResponseEntity<Page<EducationalMaterialShortViewDto>> findAll(EducationalMaterialSearchCriteria criteria,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(educationalMaterialService.findAll(page, size, criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationalMaterialFullViewDto> getById(@PathVariable UUID id){
        return ResponseEntity.ok(educationalMaterialService.getEducationalMaterial(id));
    }

    @PostMapping
    public ResponseEntity<EducationalMaterialFullViewDto> createEducationalMaterial(@Valid @RequestPart("material") CreateEducationalMaterialDto request,
                                                                                    @RequestPart("files") List<MultipartFile> files,
                                                                                    Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationalMaterialService.createEducationalMaterial(request, files, authentication.getName()));
    }

    @GetMapping("/{materialId}/files/{fileId}/download")
    public ResponseEntity<Resource> downloadEducationalMaterialFile(@PathVariable UUID materialId, @PathVariable UUID fileId){
        FileDownload download = educationalMaterialService.downloadEducationalMaterialFile(materialId, fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.fileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.originalFileName() + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducationalMaterial(@PathVariable UUID id) {
        educationalMaterialService.deleteEducationalMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
