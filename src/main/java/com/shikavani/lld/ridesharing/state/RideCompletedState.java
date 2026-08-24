package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;

public final class RideCompletedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.RIDE_COMPLETED;
    }
}
