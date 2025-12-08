package com.overgaardwood.p3projectbackend.interiordoor.pricing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaterialPriceService {

    private final MaterialPriceRepository repository;

    public List<MaterialPrice> getAll() {
        return repository.findAll();
    }

    public MaterialPrice getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public MaterialPrice getByCode(String code) {
        return repository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Material price not found: " + code));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MaterialPrice create(MaterialPrice price) {
        if (repository.existsByCode(price.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Code already exists");
        }
        return repository.save(price);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MaterialPrice update(Long id, MaterialPrice updated) {
        MaterialPrice existing = getById(id);
        existing.setName(updated.getName());
        existing.setPricePerUnit(updated.getPricePerUnit());
        existing.setUnit(updated.getUnit());
        existing.setCategory(updated.getCategory());
        existing.setUpdatedBy(getCurrentUsername());
        return repository.save(existing);
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Material price with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}
