package com.overgaardwood.p3projectbackend.interiordoor.frame;

import com.overgaardwood.p3projectbackend.interiordoor.CalcPrice;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;
import lombok.Builder;

@Builder
public class Frame implements CalcPrice {
    private final MaterialPriceService priceService;

    private final String frameMaterialCode;        // "FRAME_DOUGLAS_54MM" or "FRAME_OAK_54MM"
    private final double wallOpeningWidthCm;
    private final double wallOpeningHeightCm;
    private final double wallOpeningDepthCm;
    private final double offsetCm;
    private final double sealantGapCm;
    private final boolean includesThreshold;

    @Override
    public double calculatePrice() {
        double innerWidthCm  = wallOpeningWidthCm  - (sealantGapCm * 2); //sealant on both sides
        double innerHeightCm = wallOpeningHeightCm - (sealantGapCm * 2);
        double frameDepthCm  = wallOpeningDepthCm + offsetCm;

        // Area of three frame parts (top + left + right)
        double verticalPartsM2 = (2 * innerHeightCm * frameDepthCm) / 10_000.0;
        double topPartM2       = (innerWidthCm * frameDepthCm) / 10_000.0;

        double totalM2 = verticalPartsM2 + topPartM2;

        if (includesThreshold) {
            double thresholdM2 = (innerWidthCm * frameDepthCm) / 10_000.0;
            totalM2 += thresholdM2;
        }

        double pricePerM2 = priceService.getByCode(frameMaterialCode).getPricePerUnit();
        return totalM2 * pricePerM2;
    }

    public String describe() {
        String wood = priceService.getByCode(frameMaterialCode).getName();
        String offset = offsetCm > 0 ? "+" + offsetCm : String.valueOf(offsetCm);
        return String.format("Frame: %s, offset %s cm, sealant gap %.1f cm%s",
                wood, offset, sealantGapCm, includesThreshold ? " + threshold" : "");
    }
}
