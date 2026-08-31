package com.shikavani.lld.ridesharing.observer;

import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.model.RideStateChangeEvent;

public interface RideObserver {
    void onRideStateChanged(RideStateChangeEvent event);
}
