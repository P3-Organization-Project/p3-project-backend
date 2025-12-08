package com.overgaardwood.p3projectbackend.dtos;

import lombok.Data;

@Data
public class DoorConfigurationDto {
    private String type;
    private Double widthCm;
    private Double heightCm;
    private String coreCode;
    private String frontVeneerCode;
    private String backVeneerCode;
    private Double wallOpeningWidthCm;
    private Double wallOpeningHeightCm;
    private Double wallOpeningDepthCm;
    private Double frameOffsetCm;
    private Double sealantGapCm;
    private String frameMaterialCode;
    private Boolean frameIncludesThreshold;
    private String hingeCode;
    private String lockCode;
    private Boolean hasBottomSeal;
}
