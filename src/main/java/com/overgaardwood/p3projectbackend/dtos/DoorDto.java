package com.overgaardwood.p3projectbackend.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DoorDto {
    private Integer id;
    private String name;
    private String categoryId;
}
