package com.overgaardwood.p3projectbackend.mappers;


import com.overgaardwood.p3projectbackend.dtos.RegisterUserRequest;
import com.overgaardwood.p3projectbackend.dtos.UpdateUserRequest;
import com.overgaardwood.p3projectbackend.dtos.UserDto;
import com.overgaardwood.p3projectbackend.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //This line is only used for createdAtTime in UserDto as an exercise
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")

    //important Methods below:
    UserDto toDto(User user);

    //This method helps create new users
    User toEntity(RegisterUserRequest request);

    //Method to update a users info except password
    void update(UpdateUserRequest request, @MappingTarget User user);
}
