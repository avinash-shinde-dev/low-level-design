package com.shikavani.lld.ridesharing;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.model.*;
import com.shikavani.lld.ridesharing.repository.DriverRepository;
import com.shikavani.lld.ridesharing.repository.PassengerRepository;
import com.shikavani.lld.ridesharing.repository.RideRepository;
import com.shikavani.lld.ridesharing.service.DriverService;
import com.shikavani.lld.ridesharing.service.PassengerService;
import com.shikavani.lld.ridesharing.service.RideService;

public class RideSharingApplication {

    public static void main(String[] args) {
        // 1. create passenger
        Location passengerLocation = new Location(18.244, 57.324);
        Location passengerLocation2 = new Location(28.244, -37.324);
        Passenger passenger = new Passenger("p101", "Avinash", "Avinash@gmail.com", "9876543210", passengerLocation);
        Passenger passenger2 = new Passenger("p102", "Dynaneshwari", "Dynaneshwari@gmail.com", "8796423431", passengerLocation2);

        // create Vehicle
        Vehicle vehicle1  = new Car("Hyundai", "Creta", "MH12XY1234", 2025, VehicleType.SUV);
        Vehicle vehicle2  = new Car("Maruti", "Ertiga", "MH10XY9876", 2020, VehicleType.SUV);
        Vehicle vehicle3  = new Car("Tata", "Punch", "MH15XY1234", 2025, VehicleType.HATCHBACK);
        Vehicle vehicle4  = new Bike("Honda", "Spelndor", "MH17XY1234", 2015, VehicleType.TWO_WHEELER);

        // 2. create drivers
        Location driver1Location = new Location(16.23, 55.342);
        Driver driver1 = new Driver("d101", "Virat", "Virat@gmail.com", "9878675643", driver1Location, DriverStatus.ONLINE, vehicle1);

        Location driver2Location = new Location(20.33, 59.232);
        Driver driver2 = new Driver("d102", "Rohit", "Rohit@gmail.com", "9878675643", driver2Location, DriverStatus.ONLINE, vehicle3);

        Location driver3Location = new Location(14.13, 60.542);
        Driver driver3 = new Driver("d103", "Sachin", "Sachin@gmail.com", "9878675643", driver3Location, DriverStatus.ONLINE, vehicle4);

        // Add drivers
        DriverRepository driverRepository = new DriverRepository();
        DriverService driverService = new DriverService(driverRepository);
        driverService.addDriver(driver1);
        driverService.addDriver(driver2);
        driverService.addDriver(driver3);

        System.out.println(driverService.getAvailableDrivers());
        // Ride Service
        RideService rideService = new RideService(new RideRepository(), driverService);

        PassengerRepository passengerRepository = new PassengerRepository();
        // Create PassengerService
        PassengerService passengerService = new PassengerService(passengerRepository, rideService);
        passengerService.addPassenger(passenger);
        passengerService.addPassenger(passenger2);

        Location dropLocation = new Location(24.32, 65.56);
        Ride ride = passengerService.requestRide(passenger, passengerLocation, dropLocation, VehicleType.HATCHBACK );

        driverService.accept(ride);

    }
}

