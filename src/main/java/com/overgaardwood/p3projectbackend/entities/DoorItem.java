// src/main/java/com/overgaardwood/p3projectbackend/entities/DoorItem.java
package com.overgaardwood.p3projectbackend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "door_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoorItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "door_item_id")
    private Long doorItemId;

    // Back-reference to Case (renamed field to avoid confusion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseRef;

    @Column(name = "height")
    private Double height;

    @Column(name = "width")
    private Double width;

    @Column(name = "hinge_side")
    private String hingeSide;

    @Column(name = "opening_direction")
    private String openingDirection;

    // materialCosts list (from diagram)
    @ElementCollection
    @CollectionTable(
            name = "door_item_material_cost",
            joinColumns = @JoinColumn(name = "door_item_id")
    )
    @Column(name = "cost")
    private List<Double> materialCosts = new ArrayList<>();

    public Double getTotalMaterialCost() {
        return materialCosts.stream().mapToDouble(Double::doubleValue).sum();
    }
}