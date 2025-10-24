package com.overgaardwood.p3projectbackend.mappers;


import com.overgaardwood.p3projectbackend.dtos.DoorDto;
import com.overgaardwood.p3projectbackend.entities.Door;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface DoorMapper {
    @Mapping(target = "categoryId", source = "categoryId")
    DoorDto toDto(Door door);
}
