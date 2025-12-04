package com.overgaardwood.p3projectbackend.entities;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoorItemTest {
    @Test
    void getTotalMaterialCost_TestTotal() {
        DoorItem di = new DoorItem();
        di.getMaterialCosts().addAll(Arrays.asList(10.5, 5.0, 2.25));

        double total = di.getTotalMaterialCost();
        System.out.printf("Total Cost: %.2f%n", total);
        assertEquals(17.75, total, 1e-9, "Total should be the sum of all materialCosts");
    }

    @Test
    void getTotalMaterialCost_TestEmpty() {
        DoorItem di = new DoorItem();
        double total = di.getTotalMaterialCost();
        System.out.printf("Total Cost: %.2f%n", total);
        assertEquals(0, total, 1e-9, "Total should 0.0 for empty materialCosts");
    }
}
