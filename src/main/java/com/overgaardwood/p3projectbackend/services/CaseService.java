package com.overgaardwood.p3projectbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.Customer;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoor;
import com.overgaardwood.p3projectbackend.interiordoor.InteriorDoorRequest;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
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


import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final CustomerRepository customerRepository;
    private final DoorItemRepository doorItemRepository;
    private final PdfService pdfService;
    private final ObjectMapper objectMapper;
    private final MaterialPriceService materialPriceService;
    private final CaseMapper caseMapper;
    private final UserRepository userRepository;

    @Transactional
    public CaseDto createCase(CaseDto dto) throws JsonProcessingException {

        // 1. Get current user safely
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        // 2. Find customer
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        // 3. Create case
        Case newCase = new Case();
        newCase.setCustomer(customer);
        newCase.setSeller(currentUser);
        newCase.setDoorItems(new ArrayList<>());

        double grandTotal = 0.0;

        // 4. Process doors
        for (DoorItemDto doorDto : dto.getDoorItems()) {
            if (doorDto.getDoorConfiguration() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Door configuration is required");
            }

            // Convert object → JSON string → InteriorDoorRequest
            String configJson = objectMapper.writeValueAsString(doorDto.getDoorConfiguration());
            InteriorDoorRequest request = objectMapper.readValue(configJson, InteriorDoorRequest.class);

            InteriorDoor realDoor = request.toInteriorDoor(materialPriceService);
            double doorPrice = realDoor.calculatePrice();

            DoorItem item = new DoorItem();
            item.setCaseRef(newCase);
            item.setHeight(doorDto.getHeight());
            item.setWidth(doorDto.getWidth());
            item.setHingeSide(doorDto.getHingeSide());
            item.setOpeningDirection(doorDto.getOpeningDirection());
            item.setDoorConfigurationJson(configJson);
            item.getMaterialCosts().add(doorPrice);

            newCase.addDoorItem(item);
            grandTotal += doorPrice;
        }

        Case savedCase = caseRepository.save(newCase);

        try {
            pdfService.generateCasePdf(savedCase);
        } catch (Exception e) {
            System.err.println("PDF failed: " + e.getMessage());
        }

        return caseMapper.toDto(savedCase);
    }
}