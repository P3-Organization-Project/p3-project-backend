package com.overgaardwood.p3projectbackend.interiordoor.hardware;

import com.overgaardwood.p3projectbackend.interiordoor.CalcPrice;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.Builder;

@Builder
public class Hardware implements CalcPrice {
    private final MaterialPriceService priceService;
    private final String hingeCode;       // HINGE_TECTUS or HINGE_HAMBORG
    private final String lockCode;        // LOCK_BODA2014 or LOCK_ARRONE3313
    private final String sealCode;        // Schall EX-L only exist for now but if company wants to use others.
    private final boolean hasBottomSeal;  // Schall EX-L – only if NO frame threshold
    private final int hingeCount;         // 2 for single, 4 for double

    @Override
    public double calculatePrice() {
        double hinges = priceService.getByCode(hingeCode).getPricePerUnit() * hingeCount;
        double lock = priceService.getByCode(lockCode).getPricePerUnit();
        double seal = hasBottomSeal ? priceService.getByCode(sealCode).getPricePerUnit() : 0.0;
        return hinges + lock + seal;
    }
}
