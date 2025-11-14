// src/main/java/com/overgaardwood/p3projectbackend/services/CaseService.java
package com.overgaardwood.p3projectbackend.services;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import com.overgaardwood.p3projectbackend.mappers.CaseMapper;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.repositories.CustomerRepository;
import com.overgaardwood.p3projectbackend.repositories.DoorItemRepository;
import com.overgaardwood.p3projectbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final CaseMapper caseMapper;
    private final DoorItemRepository doorItemRepository;
    private final CustomerRepository customerRepository;
    private final PdfService pdfService;


    @Transactional
    public CaseDto createCase(CaseDto dto) {
        Case entity = caseMapper.toEntity(dto);

        entity.setCreatedDate(LocalDateTime.now());
        entity.setDealStatus(dto.getDealStatus() != null ? dto.getDealStatus() : "PENDING");

        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found")));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        User currentUser = (User) auth.getPrincipal();
        entity.setSeller(currentUser);


        final Case caseToLink = entity;

        entity.getDoorItems().forEach(di -> di.setCaseRef(caseToLink));

        entity.calculateTotalPrice();

        try {
            entity = caseRepository.save(entity);
            String pdfPath = pdfService.generateCasePdf(entity);
            System.out.println("PDF generated: " + pdfPath);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate PDF: " + e.getMessage(),
                    e);
        }

        return caseMapper.toDto(entity);
    }

    public CaseDto getCase(Long id) {
        return caseRepository.findById(id)
                .map(caseMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Case not found"));
    }

    @Transactional
    public CaseDto updateCase(Long id, CaseDto dto) {
        // 1. Get current user from JWT
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. Load the case
        Case entity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));

        // 3. OWNERSHIP check
        if (!entity.getSeller().getId().equals(currentUser.getId())
        && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own cases");
        }

        // 4. Update customer/seller
        entity.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")));

        // 5. Update deal status
        entity.setDealStatus(dto.getDealStatus());

        // 6. === MERGE DOOR ITEMS ===
        for (DoorItemDto itemDto : dto.getDoorItems()) {
            DoorItem item;
            if (itemDto.getDoorItemId() != null) {
                //if the PUT request regards a doorItem that already exist find it by doorItemId and update info
                item = doorItemRepository.findById(itemDto.getDoorItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "DoorItem not found: " + itemDto.getDoorItemId()));
            } else {
                //if the PUT request regards a doorItem that does not exist create a new object and add it to
                //the Case (dto)´s list of doorItems.
                item = new DoorItem();
                entity.getDoorItems().add(item);

            }

            item.setHeight(itemDto.getHeight());
            item.setWidth(itemDto.getWidth());
            item.setHingeSide(itemDto.getHingeSide());
            item.setOpeningDirection(itemDto.getOpeningDirection());
            item.setMaterialCosts(new ArrayList<>(itemDto.getMaterialCosts()));
            item.setCaseRef(entity);

        }

    // 7. HARD DELETE DoorItems
            if(dto.getDeleteDoorItemIds() != null) {
                for (Long deleteId : dto.getDeleteDoorItemIds()) {
                    DoorItem toDelete = doorItemRepository.findById(deleteId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DoorItem not found: " + deleteId));
                    entity.getDoorItems().remove(toDelete);
                    doorItemRepository.delete(toDelete);

                }
            }
        // 8. Recalculate total
        entity.calculateTotalPrice();
        // 9. Save
        entity = caseRepository.save(entity);

        return caseMapper.toDto(entity);
    }

    @Transactional
    public void deleteCase(Long id) {
        // 1. Get current user from JWT
        User currentUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. Load the case
        Case entity = caseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));

        // 3. OWNERSHIP CHECK
        if (!entity.getSeller().getId().equals(currentUser.getId())
                && !Role.ADMIN.equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own cases");
        }
        // 4. Delete the case
        caseRepository.delete(entity);
    }

   @Transactional(readOnly = true)
   public List<CaseDto> getCasesForCurrentUser() {
        // 1. get current user
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       User currentuser = (User) auth.getPrincipal();

       // 2. Get role
       String role = currentuser.getRole().name();

       // 3. Fetch cases
       List<Case> cases;
       if ("ADMIN".equals(role)) {
           cases = caseRepository.findAll();
       } else if ("SELLER".equals(role)) {
           cases = caseRepository.findBySeller(currentuser);
       } else {
           throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid role");
       }

       // 4 Map to DTO
       return cases.stream()
               .map(caseMapper::toDto)
               .toList();
   }
}