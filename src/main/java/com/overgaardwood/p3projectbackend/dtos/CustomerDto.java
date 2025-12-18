package com.overgaardwood.p3projectbackend.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private String companyName;
    private String address;
    private String phoneNumber;
}