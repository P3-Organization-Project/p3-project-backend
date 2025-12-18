package com.overgaardwood.p3projectbackend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.Customer;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import com.overgaardwood.p3projectbackend.mappers.CaseMapper;
import com.overgaardwood.p3projectbackend.repositories.CaseRepository;
import com.overgaardwood.p3projectbackend.repositories.CustomerRepository;
import com.overgaardwood.p3projectbackend.repositories.DoorItemRepository;
import com.overgaardwood.p3projectbackend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaseServiceTest {

    private CaseRepository caseRepository;
    private CustomerRepository customerRepository;
    private DoorItemRepository doorItemRepository;
    private PdfService pdfService;
    private ObjectMapper objectMapper;
    private MaterialPriceService materialPriceService;
    private CaseMapper caseMapper;
    private UserRepository userRepository;

    private CaseService caseService;

    @BeforeEach
    void setUp() {
        caseRepository = mock(CaseRepository.class);
        customerRepository = mock(CustomerRepository.class);
        doorItemRepository = mock(DoorItemRepository.class);
        pdfService = mock(PdfService.class);
        objectMapper = mock(ObjectMapper.class);
        materialPriceService = mock(MaterialPriceService.class);
        caseMapper = mock(CaseMapper.class);
        userRepository = mock(UserRepository.class);

        caseService = new CaseService(
                caseRepository,
                customerRepository,
                doorItemRepository,
                pdfService,
                objectMapper,
                materialPriceService,
                caseMapper,
                userRepository
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllCases_AdminReturnsAll() {
        // Setup authentication with admin user
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        // Create test cases
        Case c1 = new Case();
        c1.setCaseId(11L);
        Case c2 = new Case();
        c2.setCaseId(12L);
        List<Case> entities = List.of(c1, c2);

        // Create expected DTOs
        CaseDto dto1 = CaseDto.builder().caseId(11L).build();
        CaseDto dto2 = CaseDto.builder().caseId(12L).build();

        // Mock repository and mapper behavior
        when(caseRepository.findAll()).thenReturn(entities);
        when(caseMapper.toDto(c1)).thenReturn(dto1);
        when(caseMapper.toDto(c2)).thenReturn(dto2);

        // Execute
        List<CaseDto> result = caseService.getAllCases();

        // Verify
        verify(caseRepository, times(1)).findAll();
        verify(caseMapper, times(1)).toDto(c1);
        verify(caseMapper, times(1)).toDto(c2);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void getCasesForSeller_ReturnsOnlySellerCases() {
        // Setup seller user
        User seller = new User();
        seller.setId(2L);
        seller.setRole(Role.SELLER);

        // Create test case for seller
        Case c1 = new Case();
        c1.setCaseId(21L);
        c1.setSeller(seller);
        List<Case> entities = List.of(c1);

        // Create expected DTO
        CaseDto dto1 = CaseDto.builder().caseId(21L).build();

        // Mock repository and mapper behavior
        when(caseRepository.findBySeller(seller)).thenReturn(entities);
        when(caseMapper.toDto(c1)).thenReturn(dto1);

        // Execute
        List<CaseDto> result = caseService.getCasesForSeller(seller);

        // Verify
        verify(caseRepository, times(1)).findBySeller(seller);
        verify(caseMapper, times(1)).toDto(c1);

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
    }

    @Test
    void createCase_Success() throws Exception {
        // Setup authentication
        User seller = new User();
        seller.setId(2L);
        seller.setRole(Role.SELLER);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(seller);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Setup customer
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // Setup input DTO (no door items for simplicity)
        CaseDto inputDto = CaseDto.builder()
                .customerId(1L)
                .dealStatus("PENDING")
                .doorItems(new ArrayList<>())
                .build();

        // Setup saved case
        Case savedCase = new Case();
        savedCase.setCaseId(100L);
        savedCase.setCustomer(customer);
        savedCase.setSeller(seller);
        savedCase.setDoorItems(new ArrayList<>());
        savedCase.setTotalPrice(0.0);
        savedCase.setDealStatus("PENDING");

        when(caseRepository.save(any(Case.class))).thenReturn(savedCase);

        // Setup return DTO
        CaseDto returnDto = CaseDto.builder()
                .caseId(100L)
                .customerId(1L)
                .sellerId(2L)
                .totalPrice(0.0)
                .dealStatus("PENDING")
                .build();

        when(caseMapper.toDto(savedCase)).thenReturn(returnDto);

        // Execute
        CaseDto result = caseService.createCase(inputDto);

        // Verify
        verify(customerRepository, times(1)).findById(1L);
        verify(caseRepository, times(1)).save(any(Case.class));
        verify(caseMapper, times(1)).toDto(savedCase);

        assertNotNull(result);
        assertEquals(100L, result.getCaseId());
        assertEquals(1L, result.getCustomerId());
        assertEquals(2L, result.getSellerId());
    }
}
