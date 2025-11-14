package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.services.DoorItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/doorItems")
@RequiredArgsConstructor
public class DoorItemController {

    private final DoorItemService doorItemService;
    private final CaseRepository caseRepository;

    @GetMapping("/{id}")
    public ResponseEntity<DoorItemDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(doorItemService.getById(id));
    }

    @PostMapping
    public ResponseEntity<DoorItemDto> create(
            @RequestBody DoorItemDto dto,
            UriComponentsBuilder ucb) {

        DoorItemDto created = doorItemService.create(dto);
        URI location = ucb.path("/doorItems/{id}")
                .buildAndExpand(created.getDoorItemId())
                .toUri();
        return ResponseEntity.created(location).body(created);

    }

    @PutMapping("/{id}")
    public ResponseEntity<DoorItemDto> update(@PathVariable Long id,
                                              @RequestBody DoorItemDto dto) {
        return ResponseEntity.ok(doorItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DoorItemDto> delete(@PathVariable Long id) {
        doorItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
