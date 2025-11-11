// src/main/java/com/overgaardwood/p3projectbackend/entities/Case.java
package com.overgaardwood.p3projectbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"case\"")                     // PostgreSQL reserved word
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;

    @ManyToOne //Many = Case → One = Customer(.java)
    //The List<Case> in Customer.java is not the owner of the relationship
    //The owner is this customer field inside Case. see Customer.java
    @JoinColumn(name = "customer_id")
    //Joins Customer.java id with Case table´s customers_id column
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "deal_status")
    private String dealStatus;

    // 1-to-many: Case → List<DoorItem>
    @OneToMany(
            mappedBy = "caseRef",                 // matches field name in DoorItem
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DoorItem> doorItems = new ArrayList<>();


    public void calculateTotalPrice() {
        this.totalPrice = doorItems.stream()
                .mapToDouble(DoorItem::getTotalMaterialCost)
                .sum();
    }

    // ---- Helper methods (optional but recommended) ----
    public void addDoorItem(DoorItem item) {
        doorItems.add(item);
        item.setCaseRef(this);
    }

    public void removeDoorItem(DoorItem item) {
        doorItems.remove(item);
        item.setCaseRef(null);
    }
}