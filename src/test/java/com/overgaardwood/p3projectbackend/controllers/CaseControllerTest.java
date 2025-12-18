package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.services.CaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaseControllerTest {

    private CaseService caseService;
    private CaseRepository caseRepository;
    private CaseController caseController;

    @BeforeEach
    void setUp() {
        caseService = mock(CaseService.class);
        caseRepository = mock(CaseRepository.class);
        caseController = new CaseController(caseService, caseRepository);
    }

    @Test
    void testCreate() {
        // Create CaseDto (before id has been assigned)
        CaseDto inputDto = CaseDto.builder()
                .caseId(null)
                .customerId(1L)
                .sellerId(2L)
                .createdDate(LocalDateTime.of(2025, 1, 1, 1, 1))
                .totalPrice(999.0)
                .dealStatus("Completed")
                .doorItems(List.of())
                .deleteDoorItemIds(List.of())
                .build();

        // Create CaseDto that should match the input after the id has been assigned
        Long generatedId = 123L;
        CaseDto createdDto = CaseDto.builder()
                .caseId(generatedId)
                .customerId(1L)
                .sellerId(2L)
                .createdDate(LocalDateTime.of(2025, 1, 1, 1, 1))
                .totalPrice(999.0)
                .dealStatus("Completed")
                .doorItems(List.of())
                .deleteDoorItemIds(List.of())
                .build();

        // Mock service to return the createdDto when caseService in caseController.create is called
        when(caseService.createCase(inputDto)).thenReturn(createdDto);

        UriComponentsBuilder ucb = UriComponentsBuilder.fromPath("");

        ResponseEntity<CaseDto> response = caseController.create(inputDto, ucb);

        // Verify that caseService was called exactly once in the use of caseController.create
        verify(caseService, times(1)).createCase(inputDto);

        assertEquals(201, response.getStatusCodeValue());

        // The Location header should be /cases/{id}
        URI expectedLocation = URI.create("/cases/" + generatedId);
        assertEquals(expectedLocation.getPath(), response.getHeaders().getLocation().getPath());
        System.out.println("Expected: " + expectedLocation.getPath() + "\nActual Response: " + response.getHeaders().getLocation().getPath());

        // Match Every Field of the response
        assertEquals(createdDto.getCaseId(), response.getBody().getCaseId());
        assertEquals(createdDto.getCustomerId(), response.getBody().getCustomerId());
        assertEquals(createdDto.getSellerId(), response.getBody().getSellerId());
        assertEquals(createdDto.getTotalPrice(), response.getBody().getTotalPrice());
        assertEquals(createdDto.getDealStatus(), response.getBody().getDealStatus());
        assertEquals(createdDto.getDoorItems(), response.getBody().getDoorItems());
        assertEquals(createdDto.getDeleteDoorItemIds(), response.getBody().getDeleteDoorItemIds());
        assertEquals(createdDto.getCreatedDate(), response.getBody().getCreatedDate());
    }
}