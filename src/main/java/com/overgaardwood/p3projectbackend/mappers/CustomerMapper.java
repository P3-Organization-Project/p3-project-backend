package com.overgaardwood.p3projectbackend.mappers;

import com.overgaardwood.p3projectbackend.dtos.CustomerDto;
import com.overgaardwood.p3projectbackend.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CustomerDto dto);
    CustomerDto toDto(Customer entity);
}