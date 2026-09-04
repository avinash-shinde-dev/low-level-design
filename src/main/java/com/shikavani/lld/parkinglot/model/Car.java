package com.shikavani.lld.parkinglot.model;


import com.shikavani.lld.parkinglot.enums.Size;

public class Car extends Vehicle {

    public Car(String registrationNo, String brand, String model, Size size, String manufacturingYear) {
        super(registrationNo, brand, model, size,manufacturingYear);
    }
}
