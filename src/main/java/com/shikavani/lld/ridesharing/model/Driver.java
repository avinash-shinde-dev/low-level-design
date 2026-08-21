package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.DriverStatus;

public final class Driver extends User {

    private DriverStatus status;
    private Vehicle vehicle;

    public Driver(String driverId, String name, String email, String phoneNo, Location location, DriverStatus status, Vehicle vehicle) {
        super(driverId, name, email, phoneNo, location);
        this.status = status;
        this.vehicle = vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }



    @Override
    public String toString() {

        return "Driver{" +
                "driverId: " + super.getUserId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", email='" + super.getEmail() + '\'' +
                ", phoneNo='" + super.getPhoneNo() + '\'' +
                ", location=" + super.getLocation() +
                "status=" + status +
                '}';

    }
}
