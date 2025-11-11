// src/main/java/com/overgaardwood/p3projectbackend/controllers/CaseController.java
package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.services.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @GetMapping("/{id}")
    public ResponseEntity<CaseDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCase(id));
    }

    @GetMapping
    public ResponseEntity<List<CaseDto>> getCasesForUser(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(caseService.getCasesForCurrentUser());
    }

    @PostMapping
    public ResponseEntity<CaseDto> create(
            @RequestBody CaseDto dto,
            UriComponentsBuilder ucb) {

        CaseDto created = caseService.createCase(dto);
        URI location = ucb.path("/cases/{id}")
                .buildAndExpand(created.getCaseId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

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
}