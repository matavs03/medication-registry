package rs.ac.bg.fon.medicationregistry.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.bg.fon.medicationregistry.dtos.CreateLetterRequest;
import rs.ac.bg.fon.medicationregistry.dtos.LetterFullViewDto;
import rs.ac.bg.fon.medicationregistry.services.LetterService;

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
}
