package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.entities.Case;
import com.overgaardwood.p3projectbackend.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CaseRepository extends JpaRepository<Case, Long> {

    @EntityGraph(attributePaths = {"doorItems", "doorItems.materialCosts"})
    @Query("SELECT c FROM Case c")
    List<Case> findAllWithDoorItems();
    List<Case> findBySeller(User seller);
}