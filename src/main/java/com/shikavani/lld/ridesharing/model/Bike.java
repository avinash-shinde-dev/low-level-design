package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.VehicleType;

public class Bike extends Vehicle{
    public Bike(String brand, String model, String licensePlate, Integer manufacturingYear, VehicleType vehicleType) {
        super(brand, model, licensePlate, manufacturingYear, vehicleType);
    }
}
