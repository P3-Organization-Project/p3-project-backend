// src/main/java/com/overgaardwood/p3projectbackend/mappers/DoorItemMapper.java
package com.overgaardwood.p3projectbackend.mappers;

import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DoorItemMapper {

    @Mapping(target = "caseId", source = "caseRef.caseId")
    DoorItemDto toDto(DoorItem entity);

    @Mapping(target = "caseRef", ignore = true)
    DoorItem toEntity(DoorItemDto dto);
}