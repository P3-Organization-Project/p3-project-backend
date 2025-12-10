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

    @Value("${app.upload-dir}")
    private String uploadDir;

    // GET ALL cases for current user (or all if ADMIN)
    @GetMapping
    public ResponseEntity<List<CaseDto>> getCasesForUser(@AuthenticationPrincipal User currentUser) {
        List<CaseDto> cases = currentUser.getRole() == Role.ADMIN
                ? caseService.getAllCases()
                : caseService.getCasesForSeller(currentUser);
        return ResponseEntity.ok(cases);
    }

    // GET one case by ID (only owner or ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<CaseDto> getOne(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        CaseDto caseDto = caseService.getCaseById(id,currentUser);
        return ResponseEntity.ok(caseDto);
    }

    // CREATE case
    @PostMapping
    public ResponseEntity<CaseDto> create(@RequestBody CaseDto dto, UriComponentsBuilder ucb) {
        CaseDto created;
        try {
            created = caseService.createCase(dto);  // ← now allowed because we catch it
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid door configuration format", e);
        }

        URI location = ucb.path("/cases/{id}").buildAndExpand(created.getCaseId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    // UPDATE case (full replace — add PATCH later if needed)
    public ResponseEntity<CaseDto> update(@PathVariable Long id,
                                          @RequestBody CaseDto dto,
                                          @AuthenticationPrincipal User currentUser) {
        CaseDto updated = caseService.updateCase(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    // DELETE case + PDF file
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        caseService.deleteCase(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // DOWNLOAD PDF (owner or ADMIN)
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));

        // Security: only seller or ADMIN
        if (!caseEntity.getSeller().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only download your own cases");
        }

        File file = new File(uploadDir + "/case-" + id + ".pdf");
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PDF not found");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"case-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }


}