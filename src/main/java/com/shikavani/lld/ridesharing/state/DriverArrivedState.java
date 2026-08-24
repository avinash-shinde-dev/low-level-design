package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class DriverArrivedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ARRIVED;
    }

    @Override
    public void start(Ride ride) {
        ride.next(new RideStartedState());
    }

    @Override
    public void cancel(Ride ride) {
        releaseDriver(ride);
        ride.next(new RideCancelledState());
    }
}
