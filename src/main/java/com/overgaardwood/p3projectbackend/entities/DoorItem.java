package com.overgaardwood.p3projectbackend.entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Entity
@Table(name = "door_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoorItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "door_item_id")
    private Long doorItemId;

    // Back-reference to Case (renamed field to avoid confusion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseRef;

    @Column(name = "height")
    private Double height;

    @Column(name = "width")
    private Double width;

    @Column(name = "hinge_side")
    private String hingeSide;

    @Column(name = "opening_direction")
    private String openingDirection;

    @Column(columnDefinition = "JSONB")
    @Type(JsonType.class)
    private String doorConfigurationJson;

    // materialCosts list (from diagram)
    @ElementCollection
    @CollectionTable(
            name = "door_item_material_cost",
            joinColumns = @JoinColumn(name = "door_item_id")
    )
    @Column(name = "cost")
    private List<Double> materialCosts = new ArrayList<>();

    public Double getTotalMaterialCost() {
        return materialCosts.stream().mapToDouble(Double::doubleValue).sum();
    }

    //Calculates and sets width/height based on doorConfigurationJson.
    //Uses the same formula as InteriorDoorRequest.toInteriorDoor():
    public void calculateDimensionsFromConfig(ObjectMapper mapper) {
        if (this.doorConfigurationJson == null || this.doorConfigurationJson.isBlank()) {
            return;
        }

        try {
            var configNode = mapper.readTree(this.doorConfigurationJson);

            Double wallOpeningWidthCm = configNode.has("wallOpeningWidthCm")
                    ? configNode.get("wallOpeningWidthCm").asDouble() : null;
            Double wallOpeningHeightCm = configNode.has("wallOpeningHeightCm")
                    ? configNode.get("wallOpeningHeightCm").asDouble() : null;
            double sealantGapCm = configNode.has("sealantGapCm")
                    ? configNode.get("sealantGapCm").asDouble() : 0.1;
            double frameThicknessCm = configNode.has("frameThicknessCm")
                    ? configNode.get("frameThicknessCm").asDouble() : 0.0;
            String type = configNode.has("type")
                    ? configNode.get("type").asText() : "SINGLE";

            if (wallOpeningWidthCm != null && wallOpeningHeightCm != null) {
                double calculatedWidth = (wallOpeningWidthCm - (sealantGapCm * 2)) - (frameThicknessCm * 2);
                double calculatedHeight = (wallOpeningHeightCm - (sealantGapCm * 2)) - (frameThicknessCm * 2);

                // For double doors, each leaf is half the width
                if ("DOUBLE".equalsIgnoreCase(type)) {
                    calculatedWidth = calculatedWidth / 2;
                }

                this.width = calculatedWidth;
                this.height = calculatedHeight;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse doorConfigurationJson for dimension calculation", e);
        }
    }
}