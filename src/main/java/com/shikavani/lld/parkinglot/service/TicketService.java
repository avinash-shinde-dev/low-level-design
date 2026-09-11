package com.shikavani.lld.parkinglot.service;

import com.shikavani.lld.parkinglot.enums.TicketStatus;
import com.shikavani.lld.parkinglot.exception.InvalidTicketException;
import com.shikavani.lld.parkinglot.model.ParkingRequest;
import com.shikavani.lld.parkinglot.model.ParkingSpot;
import com.shikavani.lld.parkinglot.model.Ticket;
import com.shikavani.lld.parkinglot.repository.TicketRepository;

import java.time.Instant;
import java.util.UUID;

public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket generateTicket(ParkingRequest parkingRequest, ParkingSpot parkingSpot) {
        Ticket ticket = new Ticket(UUID.randomUUID().toString(),
                parkingRequest.user().id(),
                parkingRequest.vehicle().getRegistrationNo(),
                parkingSpot,
                Instant.now(),
                TicketStatus.ACTIVE);
        // save ticket
        return this.ticketRepository.save(ticket);
    }

    public void markTicketClosed(Ticket ticket){
        if (ticket.getTicketStatus() == TicketStatus.CLOSED) {
            throw new InvalidTicketException("Ticket already closed: " + ticket.getTicketId());
        }
        ticket.setTicketStatus(TicketStatus.CLOSED);
    }
}
