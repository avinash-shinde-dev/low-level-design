package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class RideAcceptedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ACCEPTED;
    }

    @Override
    public void driverArrived(Ride ride) {
        ride.next(new DriverArrivedState());
    }

    @Override
    public void cancel(Ride ride) {
        releaseDriver(ride);
        ride.next(new RideCancelledState());
    }
}
