package com.overgaardwood.p3projectbackend.interiordoor.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/admin/prices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MaterialPriceController {

    private final MaterialPriceService service;

    @GetMapping
    public List<MaterialPrice> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MaterialPrice getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<MaterialPrice> create(@RequestBody MaterialPrice price,
                                                UriComponentsBuilder ucb) {
        MaterialPrice created = service.create(price);
        return ResponseEntity
                .created(ucb.path("/admin/prices/{id}").build(created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public MaterialPrice update(@PathVariable Long id,
                                @RequestBody MaterialPrice price) {
        return service.update(id, price);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
