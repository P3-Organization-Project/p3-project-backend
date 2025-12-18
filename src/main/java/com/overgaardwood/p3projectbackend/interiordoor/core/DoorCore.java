package com.overgaardwood.p3projectbackend.interiordoor.core;

import com.overgaardwood.p3projectbackend.interiordoor.CalcPrice;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DoorCore implements CalcPrice {

    private final MaterialPriceService priceService;
    private final String materialCode; // e.g. "CORE_LISOCORE_38MM"

    @Override
    public double calculatePrice() {
        return priceService.getByCode(materialCode).getPricePerUnit();
    }

    public String getName() {
        return priceService.getByCode(materialCode).getName();
    }

    public String getCode() {
        return materialCode;
    }
}
