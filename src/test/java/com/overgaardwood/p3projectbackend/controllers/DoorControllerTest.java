package com.overgaardwood.p3projectbackend.controllers;

import com.overgaardwood.p3projectbackend.dtos.DoorDto;
import com.overgaardwood.p3projectbackend.entities.Door;
import com.overgaardwood.p3projectbackend.mappers.DoorMapper;
import com.overgaardwood.p3projectbackend.repositories.DoorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DoorControllerTest {

    private DoorRepository doorRepository;
    private DoorMapper doorMapper;
    private DoorController doorController;

    @BeforeEach
    void setUp() {
        doorRepository = mock(DoorRepository.class);
        doorMapper = mock(DoorMapper.class);
        doorController = new DoorController(doorRepository, doorMapper);
    }

    @Test
    void getAllDoors_withCategoryId() {

        // Initialize Objects
        String categoryId = "cat1";

        Door door = new Door();
        door.setId(1);
        door.setName("Single Door");
        door.setCategoryId(categoryId);

        DoorDto doorDto = new DoorDto(1, "Single Door", categoryId);

        // Using Mockito to mock used methods from other parts of the backend
        // This gives us an expected result, that isolates the testing to just DoorController
        when(doorRepository.findByCategoryId(categoryId)).thenReturn(List.of(door));
        when(doorMapper.toDto(door)).thenReturn(doorDto);

        List<DoorDto> result = doorController.getAllDoors(categoryId);

        // Verify amount of calls to mocked methods
        verify(doorRepository, times(1)).findByCategoryId(categoryId);
        verify(doorMapper, times(1)).toDto(door);

        // Assert Expected Results
        assertEquals(1, result.size());
        assertEquals(doorDto, result.get(0));
    }

    @Test
    void getAllDoors_withoutCategoryId() {

        // Initialize Objects
        Door door = new Door();
        door.setId(2);
        door.setName("Double Door");
        door.setCategoryId("cat2");

        DoorDto doorDto = new DoorDto(2, "Double Door", "cat2");

        // Using Mockito to mock used methods
        when(doorRepository.findAllWithCategory()).thenReturn(List.of(door));
        when(doorMapper.toDto(door)).thenReturn(doorDto);


        List<DoorDto> result = doorController.getAllDoors(null);

        // Verify amount of calls to mocked methods
        verify(doorRepository, times(1)).findAllWithCategory();
        verify(doorMapper, times(1)).toDto(door);

        // Assert Expected Results
        assertEquals(1, result.size());
        assertEquals(doorDto, result.get(0));
    }

    @Test
    void getAllDoors_noResults() {

        // Using Mockito to mock used methods
        when(doorRepository.findAllWithCategory()).thenReturn(Collections.emptyList());

        List<DoorDto> result = doorController.getAllDoors(null);

        // Verify amount of calls to mocked methods
        verify(doorRepository, times(1)).findAllWithCategory();
        verify(doorMapper, times(0)).toDto(any());

        // Assert Expected Results
        assertEquals(0, result.size());
    }
}