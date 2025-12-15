package com.overgaardwood.p3projectbackend.interiordoor;

import com.overgaardwood.p3projectbackend.interiordoor.core.DoorCore;
import com.overgaardwood.p3projectbackend.interiordoor.frame.Frame;
import com.overgaardwood.p3projectbackend.interiordoor.hardware.Hardware;
import com.overgaardwood.p3projectbackend.interiordoor.shelling.MassiveShelling;
import com.overgaardwood.p3projectbackend.interiordoor.shelling.Shelling;
import com.overgaardwood.p3projectbackend.interiordoor.shelling.VeneeredShelling;
import com.overgaardwood.p3projectbackend.interiordoor.pricing.MaterialPriceService;

public record InteriorDoorRequest(
        String type,// "SINGLE" or "DOUBLE"
        String sellerNote,

        // Single door
        double widthCm ,
        double heightCm,
        String coreCode,
        String frontVeneerCode,
        String backVeneerCode,

        // Double door (optional – can be null/0)
        Double leftWidthCm,
        Double leftHeightCm,
        String leftCoreCode,
        String leftFrontVeneerCode,
        String leftBackVeneerCode,
        String leftDoorLeafEdgeWoodType,

        Double rightWidthCm,
        Double rightHeightCm,
        String rightCoreCode,
        String rightFrontVeneerCode,
        String rightBackVeneerCode,
        String rightDoorLeafEdgeWoodType,

        // ====== FRAME ======
        Double wallOpeningWidthCm,
        Double wallOpeningHeightCm,
        Double wallOpeningDepthCm,
        Double frameThicknessCm,
        Double frameOffsetCm,
        Double sealantGapCm,
        String frameMaterialCode,
        Boolean frameIncludesThreshold,

        // ====== HARDWARE ======
        String hingeCode,
        String lockCode,
        String sealCode,
        Boolean hasBottomSeal,

        // ====== APPEARANCE ======
        String execution,
        String treatment,
        String patchColor,
        String naturalness


        ) {

    public InteriorDoor toInteriorDoor(MaterialPriceService priceService) {
        var builder = InteriorDoor.builder()
                .type(DoorType.valueOf(type.toUpperCase()))
                .priceService(priceService);


        // Build door leaf(s)
        DoorLeaf leaf = DoorLeaf.builder()
                .widthCm((wallOpeningWidthCm-(sealantGapCm*2))-frameThicknessCm*2)
                .heightCm((wallOpeningHeightCm-(sealantGapCm*2))-frameThicknessCm*2)
                .core(new DoorCore(priceService, coreCode))
                .frontShelling(createShelling(priceService, frontVeneerCode))
                .backShelling(createShelling(priceService, backVeneerCode))
                .build();

        if("SINGLE".equalsIgnoreCase(type)) {
            builder.doorLeaf(leaf);
        } else if ("DOUBLE".equalsIgnoreCase(type)) {
            DoorLeaf rightLeaf = DoorLeaf.builder()
                    .widthCm(rightWidthCm) //dette mangler at opdateres hvis automatisk udregning af dørblad skal ske.
                    .heightCm(rightHeightCm)
                    .core(new DoorCore(priceService, rightCoreCode != null ? rightCoreCode : coreCode))
                    .frontShelling(createShelling(priceService, rightFrontVeneerCode != null ? rightFrontVeneerCode : frontVeneerCode))
                    .backShelling(createShelling(priceService, rightBackVeneerCode != null ? rightBackVeneerCode : backVeneerCode))
                    .build();
            DoorLeaf leftLeaf = DoorLeaf.builder()
                    .widthCm(leftWidthCm)
                    .heightCm(leftHeightCm)
                    .core(new DoorCore(priceService, leftCoreCode != null ? leftCoreCode : coreCode))
                    .frontShelling(createShelling(priceService, leftFrontVeneerCode != null ? leftFrontVeneerCode : frontVeneerCode))
                    .backShelling(createShelling(priceService, leftBackVeneerCode != null ? leftBackVeneerCode : backVeneerCode))
                    .build();
            builder.leftLeaf(leftLeaf).rightLeaf(rightLeaf);
        }

        // BUILD FRAME
        Frame frame = Frame.builder()
                .priceService(priceService)
                .frameMaterialCode(frameMaterialCode)
                .wallOpeningWidthCm(wallOpeningWidthCm)
                .wallOpeningHeightCm(wallOpeningHeightCm)
                .wallOpeningDepthCm(wallOpeningDepthCm)
                .offsetCm(frameOffsetCm != null ? frameOffsetCm : 0.0)
                .sealantGapCm(sealantGapCm != null ? sealantGapCm : 0.1)
                .includesThreshold(frameIncludesThreshold != null ? frameIncludesThreshold : false)
                .build();
        builder.frame(frame);

        // BUILD HARDWARE
        Hardware hardware = Hardware.builder()
                .priceService(priceService)
                .hingeCode(hingeCode)
                .lockCode(lockCode)
                .sealCode(sealCode != null ? sealCode : "BOTTOM_SEAL_SCHALL")
                .hasBottomSeal(hasBottomSeal != null ? hasBottomSeal : false)
                .hingeCount("DOUBLE".equalsIgnoreCase(type) ? 4 : 2)
                .build();
        builder.hardware(hardware);

        return builder.build();
    }
    // below design pattern using strategy
    private Shelling createShelling(MaterialPriceService priceService, String code) {
        var price = priceService.getByCode(code);
        return switch (price.getCategory().toLowerCase()) {
            case "veneer" -> new VeneeredShelling(priceService, code);
            case "shelling" -> new MassiveShelling(priceService, code);
            default -> throw new IllegalArgumentException("Unknown shelling category for code: " + code);
        };
    }
}
