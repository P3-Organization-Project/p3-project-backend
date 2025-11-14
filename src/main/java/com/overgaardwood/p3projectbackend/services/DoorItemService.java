package com.overgaardwood.p3projectbackend.services;

import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import com.overgaardwood.p3projectbackend.mappers.DoorItemMapper;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.repositories.DoorItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
public class DoorItemService {

    private final DoorItemRepository doorItemRepository;
    private final CaseRepository caseRepository;
    private final DoorItemMapper doorItemMapper;

    public DoorItemDto getById(Long id) {
        return doorItemRepository.findById(id)
                .map(doorItemMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public DoorItemDto create(DoorItemDto dto) {
        // 1. Convert DTO → Entity
        DoorItem entity = doorItemMapper.toEntity(dto);

        // 2. Find the Case by caseId from DTO
        Case caseRef = caseRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));

        // 3. Security: Only owner/SELLER or ADMIN can add to this case
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!caseRef.getSeller().getId().equals(currentUser.getId())
                && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized");
        }

        // 4. Link DoorItem → Case
        entity.setCaseRef(caseRef);

        // 5. Save DoorItem
        entity = doorItemRepository.save(entity);

        // 6. Update Case total price
        caseRef.calculateTotalPrice();
        caseRepository.save(caseRef);

        // 7. convert Entity back to DTO -> Return DTO
        return doorItemMapper.toDto(entity);
    }

    public DoorItemDto update(Long id, DoorItemDto dto) {
        // 1. Find DoorItem by ID
        DoorItem entity = doorItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DoorItem not found"));

        // 2. Get the parent Case
        Case caseRef = entity.getCaseRef();

        // 3. Security: Only owner or admin
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!caseRef.getSeller().getId().equals(currentUser.getId())
        && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not Authorized to update this DoorItem");
        }

        // 4. Update fields
        entity.setHeight(dto.getHeight());
        entity.setWidth(dto.getWidth());
        entity.setHingeSide(dto.getHingeSide());
        entity.setOpeningDirection(dto.getOpeningDirection());
        entity.setMaterialCosts( new ArrayList<>(dto.getMaterialCosts()));

        // 5. Save updated DoorItem
        entity = doorItemRepository.save(entity);

        // 6. Recalculate Case total price
        caseRef.calculateTotalPrice();
        caseRepository.save(caseRef);

        // 7. convert Entity back to DTO -> Return updated DTO
        return doorItemMapper.toDto(entity);
    }

    public void delete(Long id) {
        // 1. Find DoorItem by ID
        DoorItem entity = doorItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DoorItem not found"));

        // 2. Get the parent Case
        Case caseRef = entity.getCaseRef();

        // 3. Security: Only owner or admin
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!caseRef.getSeller().getId().equals(currentUser.getId())
        && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this DoorItem");
        }

        // 4. Remove from Case's collection (important for JPA)
        caseRef.getDoorItems().remove(entity);

        // 5. Delete from DB
        doorItemRepository.delete(entity);

        // 6. Recalculate Case total price
        caseRef.calculateTotalPrice();
        caseRepository.save(caseRef);
    }


}
