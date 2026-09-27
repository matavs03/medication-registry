package rs.ac.bg.fon.medicationregistry.controllers;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.dtos.*;
import rs.ac.bg.fon.medicationregistry.services.LetterService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/letters")
public class LetterController {

    private final LetterService letterService;


    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }

    @PostMapping
    public ResponseEntity<LetterFullViewDto> createLetter(@Valid @RequestPart("letter") CreateLetterRequest request,
                                       @RequestPart("file") MultipartFile file,
                                       Authentication authentication) {
        return ResponseEntity.ok(letterService.createLetter(request,file,authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<Page<LetterShortViewDto>> findAll(LetterSearchCriteria criteria,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(letterService.findAll(criteria, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LetterFullViewDto> getLetterById(@PathVariable UUID id) {
        return ResponseEntity.ok(letterService.getLetter(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadLetter(@PathVariable UUID id){
        FileDownload download = letterService.downloadLetter(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.fileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.originalFileName() + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLetter(@PathVariable UUID id) {
        letterService.deleteLetter(id);
        return ResponseEntity.ok().build();
    }
}
