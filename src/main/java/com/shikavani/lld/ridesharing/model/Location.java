package com.shikavani.lld.ridesharing.model;

public record Location(double latitude, double longitude ) {
    public Location {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude ");
        }
        if (longitude < -90 || longitude > 90) {
            throw new IllegalArgumentException("Invalid latitude ");
        }
    }
}
