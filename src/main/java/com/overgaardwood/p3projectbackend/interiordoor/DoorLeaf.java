package com.overgaardwood.p3projectbackend.interiordoor;

import com.overgaardwood.p3projectbackend.interiordoor.core.DoorCore;
import com.overgaardwood.p3projectbackend.interiordoor.shelling.Shelling;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoorLeaf implements CalcPrice {
    private final double widthCm;
    private final double heightCm;
    private final DoorCore core;
    private final Shelling frontShelling;
    private final Shelling backShelling;

    @Override
    public double calculatePrice() {
        double corePrice = core.calculatePrice();
        double area = frontShelling.getArea(widthCm, heightCm);

        double frontPrice = frontShelling.calculatePriceForArea(area);
        double backPrice = backShelling.calculatePriceForArea(area);

        return corePrice + frontPrice + backPrice;
    }

    public String describe() {
        return String.format("%.1fx%.1f cm | Core: %s | Front: %s | Back: %s",
                widthCm, heightCm,
                core.getName(),
                frontShelling.getName(),
                backShelling.getName());
    }

}
