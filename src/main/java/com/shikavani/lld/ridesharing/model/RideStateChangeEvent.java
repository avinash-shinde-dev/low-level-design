package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import java.time.Instant;

public record RideStateChangeEvent(String rideId, RideStatus oldState, RideStatus newState, String driverId, String passengerId, Instant eventTime) { }
