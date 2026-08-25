package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class RideStartedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.RIDE_STARTED;
    }

    @Override
    public RideState complete(Ride ride) {
        System.out.println("Your ride has completed. Thank you for riding with us");
        releaseDriver(ride);
        return new RideCompletedState();
    }
}
