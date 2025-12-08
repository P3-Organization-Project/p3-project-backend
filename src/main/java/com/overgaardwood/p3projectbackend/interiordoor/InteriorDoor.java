package com.overgaardwood.p3projectbackend.interiordoor;

import com.overgaardwood.p3projectbackend.interiordoor.frame.Frame;
import com.overgaardwood.p3projectbackend.interiordoor.hardware.Hardware;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InteriorDoor implements CalcPrice {
    private final MaterialPriceService priceService;
    private final DoorType type;            // SINGLE or DOUBLE
    private final DoorLeaf doorLeaf;            // for single doors
    private final DoorLeaf leftLeaf;            // for double doors
    private final DoorLeaf rightLeaf;           // for double doors
    private final Frame frame;
    private final Hardware hardware;

    // → just add fields + add to calculatePrice() later

    @Override
    public double calculatePrice() {
        return switch (type) {
            case SINGLE -> doorLeaf.calculatePrice() + frame.calculatePrice() + hardware.calculatePrice();
            case DOUBLE -> leftLeaf.calculatePrice() + rightLeaf.calculatePrice() + frame.calculatePrice() + hardware.calculatePrice();
        };
    }

    public String describe() {
        return switch (type) {
            case SINGLE -> "Single Door: " + doorLeaf.describe();
            case DOUBLE -> "Double Door;\n Left:  " + leftLeaf.describe() +
                    "\n Right: " + rightLeaf.describe();
        };
    }
}


enum DoorType {
    SINGLE, DOUBLE
}

