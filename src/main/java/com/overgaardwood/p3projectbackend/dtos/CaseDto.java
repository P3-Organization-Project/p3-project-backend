// src/main/java/com/overgaardwood/p3projectbackend/dtos/CaseDto.java
package com.overgaardwood.p3projectbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CaseDto {
    private Long caseId;
    private Long customerId;
    private String customerName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdDate;
    private Double totalPrice;
    private String dealStatus;
    private List<DoorItemDto> doorItems = new ArrayList<>();

    //Below is adding getter setter so in updateCase in Services we can delete doors
    private List<Long> deleteDoorItemIds = new ArrayList<>();
}