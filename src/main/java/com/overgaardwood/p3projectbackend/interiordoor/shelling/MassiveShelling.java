package com.overgaardwood.p3projectbackend.interiordoor.shelling;

import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MassiveShelling implements Shelling {
    private final MaterialPriceService priceService;
    private final String shellCode; // ← e.g. "MASSIVE_OAK_SHELL"

    @Override
    public double calculatePrice() { return 0; }

    @Override
    public double calculatePriceForArea(double areaM2) {
        return priceService.getByCode(shellCode).getPricePerUnit() * areaM2;
    }

    @Override
    public double getArea(double widthCm, double heightCm) {
        return (widthCm / 100.0) * (heightCm / 100.0);
    }

    @Override
    public String getName() {
        return priceService.getByCode(shellCode).getName();
    }

    @Override
    public String getCode() {
        return shellCode;
    }
}
