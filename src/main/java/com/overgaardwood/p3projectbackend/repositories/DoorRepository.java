package com.overgaardwood.p3projectbackend.repositories;

import com.overgaardwood.p3projectbackend.entities.Door;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;



@Repository
public interface DoorRepository extends JpaRepository<Door, Integer> {
    @EntityGraph(attributePaths = "categoryId")
    //EntityGraph is added for query limitation see notes further down
    List<Door> findByCategoryId(String name);

    //NOTES:
//below are examples of methods to minimize
//the amount of queries hibernate will send
// in case of no join between Door and category tables.
// in case of eager loading between to entities hibernate will send additional queries
// for fetching related entities.
@EntityGraph(attributePaths = "categoryId")
@Query("SELECT p FROM Door p")
List<Door> findAllWithCategory();



}
