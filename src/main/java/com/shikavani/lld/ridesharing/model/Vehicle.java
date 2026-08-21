package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.VehicleType;
// Vehicle should be extensible that's why we are not marking it as sealed
public class Vehicle {
     private String brand;
     private String model;
     private String licensePlate;
     private Integer manufacturingYear;
     private VehicleType vehicleType;

    public Vehicle(String brand, String model, String licensePlate, Integer manufacturingYear, VehicleType vehicleType) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.manufacturingYear = manufacturingYear;
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
    @Override
    public String toString() {
        return "Vehicle{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", manufacturingYear=" + manufacturingYear +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
