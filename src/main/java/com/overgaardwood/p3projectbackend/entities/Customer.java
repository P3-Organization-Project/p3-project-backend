package com.overgaardwood.p3projectbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String companyName;
    private String address;

    @OneToMany(mappedBy = "customer")
    //The List<Case> in Customer.java is not the owner of the relationship.
    //The owner is the customer field inside Case.
    private List<Case> cases = new ArrayList<>();
}
