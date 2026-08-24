package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

public final class RideRequestedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.RIDE_REQUESTED;
    }

    @Override
    public void request(Ride ride) {
        // A Ride is created in REQUESTED state; requesting it again is harmless.
    }

    // This
    @Override
    public void assignDriver(Ride ride, Driver driver) {
        if (driver == null || driver.getVehicle() == null) {
            throw new IllegalArgumentException("An assigned driver must have a vehicle");
        }

        ride.setDriver(driver);
        ride.setVehicle(driver.getVehicle());
        ride.next(new DriverAssignedState());
    }

    @Override
    public void cancel(Ride ride) {
        System.out.println("Ride has been cancelled.");
        ride.next(new RideCancelledState());
    }
}
