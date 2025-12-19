// src/main/java/com/overgaardwood/p3projectbackend/mappers/CaseMapper.java
package com.overgaardwood.p3projectbackend.mappers;

import com.overgaardwood.p3projectbackend.dtos.CaseDto;
import com.overgaardwood.p3projectbackend.entities.Case;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {DoorItemMapper.class})
public interface CaseMapper {

    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerName", source = "seller.name")
    CaseDto toDto(Case entity);


    Case toEntity(CaseDto dto);

    void update(CaseDto dto, @MappingTarget Case entity);
}