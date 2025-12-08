package com.overgaardwood.p3projectbackend.quote;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, String> {
    List<Quote> findTop20ByOrderByCreatedAtDesc();
}
