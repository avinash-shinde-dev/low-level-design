package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.TicketStatus;

import java.time.Instant;

public class Ticket {
    private final String ticketId;
    private final String userId;
    private final String registrationId;
    private final ParkingSpot spot;
    private final Instant entryTime;
    private TicketStatus ticketStatus;

    public Ticket(String ticketId, String userId, String registrationId, ParkingSpot spot, Instant entryTime, TicketStatus ticketStatus) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.registrationId = registrationId;
        this.spot = spot;
        this.entryTime = entryTime;
        this.ticketStatus = ticketStatus;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", userId='" + userId + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", spot=" + spot +
                ", entryTime=" + entryTime +
                ", ticketStatus=" + ticketStatus +
                '}';
    }
}
