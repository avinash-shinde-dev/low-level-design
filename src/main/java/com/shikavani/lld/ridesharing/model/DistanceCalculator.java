package com.shikavani.lld.ridesharing.model;

// HaverSine formula
public final class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator(){}

    public static  double calculate(Location current, Location pickup){

        double lat1 = Math.toRadians(current.latitude());
        double lat2 = Math.toRadians(pickup.latitude());

        double deltaLat = Math.toRadians(pickup.latitude() - current.latitude());

        double deltaLon = Math.toRadians(pickup.longitude() - current.longitude());

        // Haversine value—the intermediate value that represents the angular separation between the two points on the Earth's surface.
        double haversineValue = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1)
                * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        // converts haversineValue into the central angle c between the two points.
        double centralAngle = 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));

        return EARTH_RADIUS_KM * centralAngle;
    }
}
