package com.overgaardwood.p3projectbackend.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String email;
    private String password;
}
