package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class RideAcceptedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ACCEPTED;
    }

    @Override
    public RideState driverArrived(Ride ride) {
        System.out.println("Your Driver has arrived");
        return new DriverArrivedState();
    }

    @Override
    public RideState cancel(Ride ride) {
        System.out.println("Ride has been cancelled.");
        releaseDriver(ride);
        return new RideCancelledState();
    }
}
