package com.shikavani.lld.ridesharing.observer;

import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.model.RideStateChangeEvent;

import java.util.List;

public interface RideObservable {
    void addObserver(RideObserver rideObserver);
    void removeObserver(RideObserver rideObserver);
    void notifyObservers(RideStateChangeEvent rideStateChangeEvent);
}
