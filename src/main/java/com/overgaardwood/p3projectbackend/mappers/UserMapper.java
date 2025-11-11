package com.overgaardwood.p3projectbackend.mappers;

import com.overgaardwood.p3projectbackend.dtos.UserDto;
import com.overgaardwood.p3projectbackend.entities.User;
import com.overgaardwood.p3projectbackend.enums.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto dto);
    UserDto toDto(User entity);

    default Role mapRole(String role) {
        return role != null ? Role.valueOf(role) : null;
    }

    default String mapRole(Role role) {
        return role != null ? role.name() : null;
    }
}