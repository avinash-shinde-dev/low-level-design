package com.shikavani.lld.parkinglot.model;


import com.shikavani.lld.parkinglot.enums.Size;

public class MotorCycle extends Vehicle {
    public MotorCycle(String registrationNo, String brand, String model, Size size, String manufacturingYear) {
        super(registrationNo, brand, model, size,manufacturingYear);
    }
}
