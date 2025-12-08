package com.overgaardwood.p3projectbackend.interiordoor.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MaterialPriceRepository extends JpaRepository<MaterialPrice, Long> {
    Optional<MaterialPrice> findByCode(String code);
    boolean existsByCode(String code);
}
