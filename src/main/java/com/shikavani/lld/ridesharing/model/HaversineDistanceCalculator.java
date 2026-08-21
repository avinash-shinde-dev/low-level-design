package com.shikavani.lld.ridesharing.model;

// HaverSine formula
public final class HaversineDistanceCalculator implements DistanceCalculator{

    private static final double EARTH_RADIUS_KM = 6371.0;

    public HaversineDistanceCalculator(){}

    @Override
    public double calculate(Location from, Location to){

        double lat1 = Math.toRadians(from.latitude());
        double lat2 = Math.toRadians(to.latitude());

        double deltaLat = Math.toRadians(to.latitude() - from.latitude());

        double deltaLon = Math.toRadians(to.longitude() - from.longitude());

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
