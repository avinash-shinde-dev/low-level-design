package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class RideStartedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.RIDE_STARTED;
    }

    @Override
    public void complete(Ride ride) {
        releaseDriver(ride);
        ride.next(new RideCompletedState());
    }
}
