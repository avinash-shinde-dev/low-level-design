package com.shikavani.lld.parkinglot.service;

import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.exception.InvalidParkingSpotException;
import com.shikavani.lld.parkinglot.exception.InvalidTicketException;
import com.shikavani.lld.parkinglot.exception.ParkingSpotNotAvailableException;
import com.shikavani.lld.parkinglot.manager.ParkingSpotManager;
import com.shikavani.lld.parkinglot.model.*;
import com.shikavani.lld.parkinglot.repository.ParkingRepository;
import com.shikavani.lld.parkinglot.strategy.pricing.PricingStrategy;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class ParkingService {
    private final ParkingRepository parkingRepository;
    private final ParkingSpotManager parkingSpotManager;
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final PricingStrategy pricingStrategy;

    public ParkingService(ParkingRepository parkingRepository, ParkingSpotManager parkingSpotManager, TicketService ticketService, PaymentService paymentService, PricingStrategy pricingStrategy) {
        this.parkingRepository = parkingRepository;
        this.parkingSpotManager = parkingSpotManager;
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.pricingStrategy = pricingStrategy;
    }

    public Ticket park(ParkingRequest parkingRequest){
        System.out.println("Parking your vehicle");

        // for now I can hard code the value, but again we need to figure out the parkinglot as well.
        final String parkingLotId = "PL1";
        // 1.find if the spot is available or not based on strategy
        Optional<ParkingSpot> spot = this.parkingSpotManager.findAvailableParkingSpot(parkingLotId, parkingRequest.vehicle().getSize());

        ParkingSpot parkingSpot = spot.orElseThrow(() -> new ParkingSpotNotAvailableException("Parking Not Found Exception"));
        if(parkingSpot.canFit(parkingRequest.vehicle()))
            parkingSpot.park(parkingRequest.vehicle());
        else{
            throw new InvalidParkingSpotException("Cannot park vehicle as vehicle can't fit in the spot");
        }

        return ticketService.generateTicket(parkingRequest, parkingSpot);
    }

    public Receipt unpark(UnparkingRequest unparkingRequest){
        System.out.println("Unpark your vehicle ...");
        ParkingSpot parkingSpot = unparkingRequest.ticket().getSpot();
        // 1. find the parking spot
        parkingSpot.unPark();

        this.ticketService.markTicketClosed(unparkingRequest.ticket());

        // Assumption is that if the provided pricing strategy unsupported then we will fall back on the flat strategy
        Optional<Fee> fee = this.pricingStrategy.calculateFee(unparkingRequest.ticket());

        Fee f = fee.orElseGet(() -> new Fee(BigDecimal.ZERO, Currency.getInstance("INR")));
        PaymentDetails details = new CashDetails(f);
        PaymentRequest paymentRequest = new PaymentRequest(unparkingRequest.ticket(), details);
        PaymentResponse paymentResponse = this.paymentService.pay(paymentRequest);

        System.out.println("Payment Status: " + paymentResponse);

        return new Receipt(UUID.randomUUID().toString(), unparkingRequest.user().id(), unparkingRequest.ticket().getRegistrationId(), f, Instant.now());
    }

    public String renderParkingStatus() {
        StringBuilder sb = new StringBuilder();
        this.parkingRepository.findAll().forEach(lot -> appendLot(sb, lot));
        return sb.toString();
    }

    public void displayParking() {
        System.out.println(renderParkingStatus());
    }

    private void appendLot(StringBuilder sb, ParkingLot lot) {
        sb.append("Parking Lot: ").append(lot.parkingId()).append(System.lineSeparator());
        lot.floors().stream()
                .sorted(Comparator.comparing(Floor::level))
                .forEach(floor -> appendFloor(sb, floor));
    }

    private void appendFloor(StringBuilder sb, Floor floor) {
        int total = floor.parkingSpotMap().values().stream().mapToInt(List::size).sum();
        long available = floor.parkingSpotMap().values().stream()
                .flatMap(List::stream)
                .filter(ParkingSpot::isAvailable)
                .count();

        sb.append(String.format("%n=== Floor %s (Level %d) — %d/%d available ===%n",
                floor.floorId(), floor.level(), available, total));

        for (Size size : Size.values()) {
            List<ParkingSpot> spots = floor.parkingSpotMap().getOrDefault(size, List.of());
            if (spots.isEmpty()) continue;

            long freeInBucket = spots.stream().filter(ParkingSpot::isAvailable).count();
            sb.append(String.format("  %-8s (%d/%d free)%n", size, freeInBucket, spots.size()));

            spots.stream()
                    .sorted(Comparator.comparing(ParkingSpot::getSpotId))
                    .forEach(spot -> sb.append(String.format("    [%s] %-4s %s%n",
                            spot.isAvailable() ? " " : "X",
                            spot.getSpotId(),
                            spot.isAvailable() ? "-" : describe(spot.getParkedVehicle()))));
        }
    }

    private String describe(Vehicle vehicle) {
        return vehicle.getRegistrationNo() + " (" + vehicle.getBrand() + " " + vehicle.getModel() + ")";
    }

}
