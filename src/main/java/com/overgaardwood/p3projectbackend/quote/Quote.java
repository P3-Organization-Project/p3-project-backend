package com.overgaardwood.p3projectbackend.quote;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quote {

    @Id
    private String id; // UUID as string

    @Column(columnDefinition = "TEXT")
    private String requestJson; // store the full InteriorDoorRequest as JSON string

    private String description;

    private double totalPriceExVat;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
