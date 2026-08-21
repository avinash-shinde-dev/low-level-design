package com.shikavani.lld.ridesharing.model;


// Vehicle should be extensible that's why we are not marking it as sealed
public class Vehicle {
     private String brand;
     private String model;
     private String licensePlate;
     private Integer manufacturingYear;

    public Vehicle(String brand, String model, String licensePlate, Integer manufacturingYear) {
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.manufacturingYear = manufacturingYear;
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", manufacturingYear=" + manufacturingYear +
                '}';
    }
}
