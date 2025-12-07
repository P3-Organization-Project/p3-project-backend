package com.overgaardwood.p3projectbackend.services;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaseServiceTest {

    private CaseRepository caseRepository;
    private UserRepository userRepository;
    private CaseMapper caseMapper;
    private DoorItemRepository doorItemRepository;
    private CustomerRepository customerRepository;
    private PdfService pdfService;

    private CaseService caseService;

    @BeforeEach
    void setUp() {
        caseRepository = mock(CaseRepository.class);
        userRepository = mock(UserRepository.class);
        caseMapper = mock(CaseMapper.class);
        doorItemRepository = mock(DoorItemRepository.class);
        customerRepository = mock(CustomerRepository.class);
        pdfService = mock(PdfService.class);

        caseService = new CaseService(
                caseRepository,
                userRepository,
                caseMapper,
                doorItemRepository,
                customerRepository,
                pdfService
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCasesForCurrentUser_AdminReturnsAll() {
        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(admin);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        Case c1 = new Case();
        Case c2 = new Case();
        List<Case> entities = List.of(c1, c2);

        CaseDto dto1 = CaseDto.builder().caseId(11L).build();
        CaseDto dto2 = CaseDto.builder().caseId(12L).build();

        when(caseRepository.findAll()).thenReturn(entities);
        when(caseMapper.toDto(c1)).thenReturn(dto1);
        when(caseMapper.toDto(c2)).thenReturn(dto2);

        List<CaseDto> result = caseService.getCasesForCurrentUser();

        verify(caseRepository, times(1)).findAll();
        verify(caseRepository, never()).findBySeller(any());
        verify(caseMapper, times(1)).toDto(c1);
        verify(caseMapper, times(1)).toDto(c2);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void getCasesForCurrentUser_Seller() {
        User seller = new User();
        seller.setId(2L);
        seller.setRole(Role.SELLER);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(seller);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Make cases for seller
        Case c1 = new Case();
        c1.setSeller(seller);
        List<Case> entities = List.of(c1);

        CaseDto dto1 = CaseDto.builder().caseId(21L).build();

        when(caseRepository.findBySeller(seller)).thenReturn(entities);
        when(caseMapper.toDto(c1)).thenReturn(dto1);

        List<CaseDto> result = caseService.getCasesForCurrentUser();

        verify(caseRepository, times(1)).findBySeller(seller);
        verify(caseRepository, never()).findAll();
        verify(caseMapper, times(1)).toDto(c1);

        assertEquals(1, result.size());
        assertEquals(dto1, result.get(0));
    }

    // Test for when (or if) user without case permission is added
    /* @Test
    void getCasesForCurrentUser_InvalidRole() {
        User other = new User();
        other.setId(3L);

        // other.setRole(Role.**Non-Permitted User Type**);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(other);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> caseService.getCasesForCurrentUser());

        assertEquals("403 FORBIDDEN \"Invalid role\"", ex.getMessage());
        verifyNoInteractions(caseRepository); // Repository should not be called
    } */

}