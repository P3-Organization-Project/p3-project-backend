package com.overgaardwood.p3projectbackend.dtos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;  // "SELLER" or "ADMIN"
}