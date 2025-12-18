package com.overgaardwood.p3projectbackend.services;

import com.overgaardwood.p3projectbackend.dtos.CustomerDto;
import com.overgaardwood.p3projectbackend.entities.Customer;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import com.overgaardwood.p3projectbackend.mappers.CustomerMapper;
import com.overgaardwood.p3projectbackend.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerDto createCustomer(CustomerDto dto, User currentUser) {
        Customer customer = customerMapper.toEntity(dto);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toDto(saved);
    }

    public List<CustomerDto> getAllCustomers(User currentUser) {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public CustomerDto getCustomerById(Long id, User currentUser) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return customerMapper.toDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto dto, User currentUser) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setCompanyName(dto.getCompanyName());
        customer.setAddress(dto.getAddress());
        customer.setPhoneNumber(dto.getPhoneNumber());

        Customer saved = customerRepository.save(customer);
        return customerMapper.toDto(saved);
    }

    @Transactional
    public void deleteCustomer(Long id, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete customers");
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customerRepository.delete(customer);
    }
}