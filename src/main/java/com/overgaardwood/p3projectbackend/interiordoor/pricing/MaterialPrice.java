package com.overgaardwood.p3projectbackend.interiordoor.pricing;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "material_prices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaterialPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;                    // e.g. "VENEER_OAK", "CORE_TUBULAR", "GLASS_8MM"

    @Column(nullable = false)
    private String name;                    // name in sheet: "Oak Veneer 0.6mm"

    @Column(nullable = false)
    private Double pricePerUnit;            // Price per m², per meter, or per piece

    private String unit;                    // "m²", "meter", "piece"

    private String category;                // "veneer", "core", "glass", "edge", "hardware"

    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String updatedBy;               //Admin username

    // ONE METHOD handles both create and update
    @PrePersist
    @PreUpdate
    private void updateTimestamps() {
        updatedAt = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = updatedAt;
        }
    }
}
