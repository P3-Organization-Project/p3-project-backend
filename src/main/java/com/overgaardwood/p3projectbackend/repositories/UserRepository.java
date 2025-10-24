package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.dtos.UserDto;
import com.overgaardwood.p3projectbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // No custom methods needed!
}
