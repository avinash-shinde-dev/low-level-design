package com.shikavani.lld.parkinglot.model;


import com.shikavani.lld.parkinglot.enums.Size;

public class Car extends Vehicle {

    public Car(String registrationNo, String brand, String model, Size size, String manufacturingYear) {
        super(registrationNo, brand, model, size,manufacturingYear);
    }

    @Override
    public String toString() {
        return "Car{" +
                "registrationNo='" + super.getRegistrationNo() + '\'' +
                ", brand='" + super.getBrand() + '\'' +
                ", model='" + super.getModel() + '\'' +
                ", size=" + super.getSize() +
                ", manufacturingYear='" + super.getManufacturingYear() + '\'' +
                '}';
    }
}
