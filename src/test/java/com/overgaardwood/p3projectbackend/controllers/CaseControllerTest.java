package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.services.CaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testCreate() throws Exception {
        // Create input CaseDto (before id assignment)
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

        // Create CaseDto that matches after id assignment
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

        // Mock service to return createdDto
        when(caseService.createCase(inputDto)).thenReturn(createdDto);

        UriComponentsBuilder ucb = UriComponentsBuilder.fromPath("");

        // Execute
        ResponseEntity<CaseDto> response = caseController.create(inputDto, ucb);

        // Verify service was called once
        verify(caseService, times(1)).createCase(inputDto);

        // Verify response status code
        assertEquals(201, response.getStatusCodeValue());

        // Verify Location header
        URI expectedLocation = URI.create("/cases/" + generatedId);
        assertEquals(expectedLocation.getPath(), response.getHeaders().getLocation().getPath());
        System.out.println("Expected: " + expectedLocation.getPath() + "\nActual Response: " + response.getHeaders().getLocation().getPath());

        // Verify all fields of response body
        assertNotNull(response.getBody());
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
