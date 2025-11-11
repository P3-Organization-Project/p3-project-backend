// src/main/java/com/overgaardwood/p3projectbackend/dtos/DoorItemDto.java
package com.overgaardwood.p3projectbackend.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoorItemDto {
    private Long doorItemId;
    private Long caseId;
    private Double height;
    private Double width;
    private String hingeSide;
    private String openingDirection;
    private List<Double> materialCosts = new ArrayList<>();
}