package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;

public final class RideCancelledState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.RIDE_CANCELLED;
    }
}
