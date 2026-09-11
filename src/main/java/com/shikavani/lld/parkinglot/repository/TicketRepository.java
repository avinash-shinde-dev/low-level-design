package com.shikavani.lld.parkinglot.repository;

import com.shikavani.lld.parkinglot.model.Ticket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TicketRepository implements InMemoryRepository<String, Ticket> {
    private final Map<String, Ticket> ticketConcurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        ticketConcurrentHashMap.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    @Override
    public Ticket findById(String ticketId) {
        return ticketConcurrentHashMap.get(ticketId);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketConcurrentHashMap.values().stream().toList();
    }
}
