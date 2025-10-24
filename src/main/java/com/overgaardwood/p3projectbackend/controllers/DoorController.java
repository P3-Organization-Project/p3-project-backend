package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.DoorDto;
import com.overgaardwood.p3projectbackend.dtos.UserDto;
import com.overgaardwood.p3projectbackend.entities.Door;
import com.overgaardwood.p3projectbackend.mappers.DoorMapper;
import com.overgaardwood.p3projectbackend.repositories.DoorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@AllArgsConstructor
@RequestMapping("/doors")
public class DoorController {

    private final DoorRepository doorRepository;
    private final DoorMapper doorMapper;

    @GetMapping
    public List<DoorDto> getAllDoors(//this method is an exercise in request paramaeters
                                     @RequestParam(required = false, name = "categoryId") String categoryId
    ) {
        List<Door> door;
        if (categoryId != null){
            door = doorRepository.findByCategoryId(categoryId);
        } else {
            door = doorRepository.findAllWithCategory();
        }

            return door
                    .stream()
                    .map(doorMapper::toDto)
                    .toList();

    }
}