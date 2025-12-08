// src/main/java/com/overgaardwood/p3projectbackend/controllers/CaseController.java
package com.overgaardwood.p3projectbackend.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.services.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private final CaseRepository caseRepository;
/*
    @GetMapping("/{id}")
    public ResponseEntity<CaseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCase(id));
    }

 */
/*
    @GetMapping
    public ResponseEntity<List<CaseDto>> getCasesForUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(caseService.getCasesForCurrentUser());
    }

 */

    @Value("${app.upload-dir}")
    private String uploadDir; // spring injects the value
    //dynamic endpoint for downloading case PDF
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {

        //1. Find case from DB via repo.
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
/*
        //Security check only owner/SELLER or ADMIN of the case can download.
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!caseEntity.getSeller().getId().equals(currentUser.getId()) &&
                !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only download your own cases");
        }

 */

        //check uploads directory for the requested pdf
        String filePath = uploadDir + "/case-" + id + ".pdf";
        File file = new File(filePath);
        //check if the pdf exist
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF not found");
        }

        //Wraps the file in a spring Resource object.
        //this makes the file "servable" over HTTP.
        //Spring's ResponseEntity needs a Resource to stream the file(without loading it all into memory)
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()          //tells the browser/Postman to download it as a file (not display inline)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"case-" + id + ".pdf\"")
                //ensures the client (browser/Postman) knows it's a PDF
                .contentType(MediaType.APPLICATION_PDF)
                //Attaches the file content as the response body. Spring streams it directly from disk.
                .body(resource);
    }


    @PostMapping
    public ResponseEntity<CaseDto> create(
            @RequestBody CaseDto dto,
            UriComponentsBuilder ucb) {

        CaseDto created;
        try {
            created = caseService.createCase(dto);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid door configuration format");
        }
        URI location = ucb.path("/cases/{id}")
                .buildAndExpand(created.getCaseId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
/*
    @PutMapping("/{id}")
    public ResponseEntity<CaseDto> update(@PathVariable Long id,
                                          @RequestBody CaseDto dto) {
        return ResponseEntity.ok(caseService.updateCase(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }

 */
}