package com.overgaardwood.p3projectbackend.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overgaardwood.p3projectbackend.dtos.DoorConfigurationDto;
import com.overgaardwood.p3projectbackend.dtos.DoorItemDto;
import com.overgaardwood.p3projectbackend.entities.DoorItem;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DoorItemMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "caseId", source = "caseRef.caseId")
    @Mapping(target = "doorConfiguration", ignore = true)
    public abstract DoorItemDto toDto(DoorItem entity);

    @Mapping(target = "caseRef", ignore = true)
    public abstract DoorItem toEntity(DoorItemDto dto);

    @AfterMapping
    protected void deserializeDoorConfiguration(DoorItem entity, @MappingTarget DoorItemDto dto) {
        // Deserialize doorConfiguration JSON to DTO
        if (entity.getDoorConfigurationJson() != null && !entity.getDoorConfigurationJson().isBlank()) {
            try {
                DoorConfigurationDto config = objectMapper.readValue(
                        entity.getDoorConfigurationJson(),
                        DoorConfigurationDto.class
                );
                dto.setDoorConfiguration(config);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize doorConfigurationJson", e);
            }
        }

        // Calculate dimensions if not already set
        if (entity.getWidth() == null || entity.getHeight() == null) {
            entity.calculateDimensionsFromConfig(objectMapper);
            dto.setWidth(entity.getWidth());
            dto.setHeight(entity.getHeight());
        }
    }
}
