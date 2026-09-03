package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class DriverAssignedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ASSIGNED;
    }

    @Override
    public RideState accept(Ride ride) {
        return new RideAcceptedState();
    }

    @Override
    public RideState reject(Ride ride) {
        releaseDriver(ride);
        return new RideRequestedState();
    }

    @Override
    public RideState cancel(Ride ride) {
        releaseDriver(ride);
        return new RideCancelledState();
    }
}
