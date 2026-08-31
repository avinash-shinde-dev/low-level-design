package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import java.time.Instant;

public record RideStateChangeEvent(Ride ride, RideStatus oldState, RideStatus newState, Instant eventTime) { }
