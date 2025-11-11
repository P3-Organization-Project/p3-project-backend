package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.entities.DoorItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoorItemRepository extends JpaRepository<DoorItem, Long> {


}