package com.shikavani.lld.parkinglot.model;

import java.time.Instant;

public record Ticket(String ticketId, String userId, String registrationNo, ParkingSpot spot, Instant entryTime) {

}
