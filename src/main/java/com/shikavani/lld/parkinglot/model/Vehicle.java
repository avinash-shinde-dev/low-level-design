package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.Size;

public class Vehicle {
    private final String registrationNo;
    private final String brand;
    private final String model;
    private final Size size;
    private final String manufacturingYear;

    public Vehicle(String registrationNo, String brand, String model, Size size, String manufacturingYear) {
        this.registrationNo = registrationNo;
        this.brand = brand;
        this.model = model;
        this.size = size;
        this.manufacturingYear = manufacturingYear;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Size getSize() {
        return size;
    }

    public String getManufacturingYear() {
        return manufacturingYear;
    }
}
