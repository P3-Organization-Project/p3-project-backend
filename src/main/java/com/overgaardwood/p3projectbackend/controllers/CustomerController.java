package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.CustomerDto;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers(@AuthenticationPrincipal User currentUser) {
        List<CustomerDto> customers = customerService.getAllCustomers(currentUser);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        CustomerDto customer = customerService.getCustomerById(id, currentUser);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(
            @RequestBody CustomerDto dto,
            @AuthenticationPrincipal User currentUser,
            UriComponentsBuilder ucb) {
        CustomerDto created = customerService.createCustomer(dto, currentUser);
        URI location = ucb.path("/customers/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto dto,
            @AuthenticationPrincipal User currentUser) {
        CustomerDto updated = customerService.updateCustomer(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        customerService.deleteCustomer(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
