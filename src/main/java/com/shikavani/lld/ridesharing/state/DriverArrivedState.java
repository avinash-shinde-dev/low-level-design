package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class DriverArrivedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ARRIVED;
    }

    @Override
    public RideState start(Ride ride) {
        System.out.println("Your ride has started ...");
        return new RideStartedState();
    }

    @Override
    public RideState cancel(Ride ride) {
        System.out.println("Ride has been cancelled.");
        releaseDriver(ride);
        return new RideCancelledState();
    }
}
