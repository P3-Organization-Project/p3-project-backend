package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Below: A custom method so we can use a service
    //to provide us a search request for email.
    //used for authentication.
    Optional<User> findByEmail(String email);

}