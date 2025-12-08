package com.overgaardwood.p3projectbackend.interiordoor;

import com.overgaardwood.p3projectbackend.interiordoor.core.DoorCore;
import com.overgaardwood.p3projectbackend.interiordoor.shelling.VeneeredShelling;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doors")
@RequiredArgsConstructor
public class InteriorDoorController {   // ← removed @PreAuthorize temporarily

    private final MaterialPriceService priceService;

    @PostMapping("/calculate")
    @PreAuthorize("permitAll()")  // ← allow everyone to calculate price
    public CalculationResponse calculate(@RequestBody InteriorDoorRequest request) {
        InteriorDoor door = request.toInteriorDoor(priceService);
        return new CalculationResponse(
                door.describe(),
                door.calculatePrice()
        );
    }
}

