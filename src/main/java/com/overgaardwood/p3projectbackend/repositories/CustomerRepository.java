package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
