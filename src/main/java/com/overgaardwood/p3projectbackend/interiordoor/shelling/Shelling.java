package com.overgaardwood.p3projectbackend.interiordoor.shelling;

import com.overgaardwood.p3projectbackend.interiordoor.CalcPrice;

public interface Shelling extends CalcPrice {
    double getArea(double widthCm, double heightCm);
    double calculatePriceForArea(double areM2);
    String getName();
    String getCode();
}
