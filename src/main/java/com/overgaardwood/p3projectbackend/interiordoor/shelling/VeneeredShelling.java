package com.overgaardwood.p3projectbackend.interiordoor.shelling;

import com.overgaardwood.p3projectbackend.interiordoor.CalcPrice;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VeneeredShelling implements Shelling {

    private final MaterialPriceService priceService;
    private final String veneerCode;

    @Override
    public double calculatePrice() {
        return 0; // not used
    }

    public double calculatePriceForArea(double areaM2) {
        return priceService.getByCode(veneerCode).getPricePerUnit() * areaM2;
    }

    @Override
    public double getArea(double widthCm, double heightCm) {
        return (widthCm / 100.0) * (heightCm / 100.0);
    }

    @Override
    public String getName() {
        return priceService.getByCode(veneerCode).getName();
    }

    @Override
    public String getCode() {
        return veneerCode;
    }
}
