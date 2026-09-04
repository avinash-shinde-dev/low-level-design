package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.Size;

public class Truck extends Vehicle{
    public Truck(String registrationNo, String brand, String model, Size size, String manufacturingYear) {
        super(registrationNo, brand, model, size,manufacturingYear);
    }
}
