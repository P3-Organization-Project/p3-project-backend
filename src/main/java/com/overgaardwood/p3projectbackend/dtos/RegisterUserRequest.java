package com.overgaardwood.p3projectbackend.dtos;


import lombok.Data;

//Note
//this could also have been named Dto at the end instead of Request
//both are valid naming conventions. in this case Request better aligns.
@Data
public class RegisterUserRequest {
    private String name;
    private String email;
    private String password;
}
