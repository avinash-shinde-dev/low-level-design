package com.shikavani.lld.parkinglot;

import com.shikavani.lld.parkinglot.enums.PaymentMode;
import com.shikavani.lld.parkinglot.enums.PricingStrategyType;
import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.enums.SpotAssignmentStrategyType;
import com.shikavani.lld.parkinglot.exception.InvalidTicketException;
import com.shikavani.lld.parkinglot.exception.ParkingSpotNotAvailableException;
import com.shikavani.lld.parkinglot.manager.ParkingSpotManager;
import com.shikavani.lld.parkinglot.model.*;
import com.shikavani.lld.parkinglot.registry.StrategyRegistry;
import com.shikavani.lld.parkinglot.repository.ParkingRepository;
import com.shikavani.lld.parkinglot.repository.TicketRepository;
import com.shikavani.lld.parkinglot.service.ParkingService;
import com.shikavani.lld.parkinglot.service.PaymentService;
import com.shikavani.lld.parkinglot.service.TicketService;
import com.shikavani.lld.parkinglot.strategy.payment.*;
import com.shikavani.lld.parkinglot.strategy.pricing.FlatPricingStrategy;
import com.shikavani.lld.parkinglot.strategy.pricing.HourlyPricingStrategy;
import com.shikavani.lld.parkinglot.strategy.pricing.PricingStrategy;
import com.shikavani.lld.parkinglot.strategy.spotassignment.FirstAvailableParkingSpotAssignmentStrategy;
import com.shikavani.lld.parkinglot.strategy.spotassignment.RandomParkingSpotAssignmentStrategy;
import com.shikavani.lld.parkinglot.strategy.spotassignment.SpotAssignmentStrategy;

import java.math.BigDecimal;
import java.util.Currency;

public class ParkingLotManagement {

    public static void main(String[] args) {

        // ---- Setup ----
        StrategyRegistry<PaymentMode, PaymentStrategy> paymentStrategies = StrategyRegistry.<PaymentMode, PaymentStrategy>builder()
                .register(PaymentMode.CASH, new CashPaymentStrategy())
                .register(PaymentMode.UPI, new UPIPaymentStrategy())
                .register(PaymentMode.CREDIT_CARD, new CreditCardStrategy())
                .register(PaymentMode.DEBIT_CARD, new DebitCardStrategy())
                .build();

        StrategyRegistry<PricingStrategyType, PricingStrategy> pricingStrategies = StrategyRegistry.<PricingStrategyType, PricingStrategy>builder()
                .register(PricingStrategyType.FLAT_CHARGES, new FlatPricingStrategy())
                .register(PricingStrategyType.HOURLY_CHARGES, new HourlyPricingStrategy())
                .build();

        StrategyRegistry<SpotAssignmentStrategyType, SpotAssignmentStrategy> spotStrategies = StrategyRegistry.<SpotAssignmentStrategyType, SpotAssignmentStrategy>builder()
                .register(SpotAssignmentStrategyType.RANDOM, new RandomParkingSpotAssignmentStrategy())
                .register(SpotAssignmentStrategyType.FIRST_AVAILABLE, new FirstAvailableParkingSpotAssignmentStrategy())
                .build();

        ParkingRepository parkingRepository = new ParkingRepository();
        ParkingSpotManager parkingSpotManager = new ParkingSpotManager(
                spotStrategies.getOrThrow(SpotAssignmentStrategyType.FIRST_AVAILABLE), parkingRepository);

        TicketService ticketService = new TicketService(new TicketRepository());
        PaymentService paymentService = new PaymentService(paymentStrategies.getOrThrow(PaymentMode.CASH));
        ParkingService parkingService = new ParkingService(
                parkingRepository, parkingSpotManager, ticketService, paymentService,
                pricingStrategies.getOrThrow(PricingStrategyType.HOURLY_CHARGES));

        // ---- UC1: Happy path — park and unpark a car, pay cash ----
        section("UC1: Happy path — Car parks, pays cash, leaves");
        User user101 = new User("u101", "Avinash", "abc@gmail.com", "9876543210",
                new Car("MH12HJ1234", "Hyundai", "Creta", Size.MEDIUM, "2026"));

        Ticket ticket1 = parkingService.park(new ParkingRequest(user101, user101.vehicle()));
        System.out.println("Issued: " + ticket1);
        Receipt receipt1 = parkingService.unpark(new UnparkingRequest(ticket1, user101));
        System.out.println("Receipt: " + receipt1);

        // ---- UC2: Multiple vehicle types share the lot ----
        section("UC2: Motorcycle and Truck park alongside each other");
        User user102 = new User("u102", "Ram", "ram@gmail.com", "8974543210",
                new MotorCycle("MH15HJ4321", "Bajaj", "Pulsar NS160", Size.SMALL, "2020"));
        User user103 = new User("u103", "Priya", "priya@gmail.com", "9988776655",
                new Truck("MH14ZW4321", "Tata", "Ace Gold", Size.LARGE, "2015"));

        Ticket ticket2 = parkingService.park(new ParkingRequest(user102, user102.vehicle()));
        Ticket ticket3 = parkingService.park(new ParkingRequest(user103, user103.vehicle()));
        System.out.println("Motorcycle spot: " + ticket2.getSpot().getSpotId());
        System.out.println("Truck spot: " + ticket3.getSpot().getSpotId());
        parkingService.displayParking();

        // ---- UC3: Lot full for a vehicle type that can only fit one spot size ----
        section("UC3: Fill every LARGE spot, then try to park one more truck");
//        for (int i = 0; i < 9; i++) { // 3 floors x 3 LARGE spots each = 9 total
//            Vehicle filler = new Truck("FILL-TRUCK-" + i, "Ashok Leyland", "Filler", Size.LARGE, "2020");
//            parkingService.park(new ParkingRequest(user103, filler));
//        }
        try {
            Vehicle oneTooMany = new Truck("MH14ZW9999", "Tata", "Overflow", Size.LARGE, "2020");
            parkingService.park(new ParkingRequest(user103, oneTooMany));
        } catch (ParkingSpotNotAvailableException e) {
            System.out.println("Expected failure — lot is full for this vehicle: " + e.getMessage());
        }

        // ---- UC4: Smaller vehicle overflows into a larger spot once SMALL is exhausted ----
        section("UC4: Fill every SMALL spot, then park one more motorcycle");
//        for (int i = 0; i < 9; i++) { // 3 floors x 3 SMALL spots each = 9 total
//            Vehicle filler = new MotorCycle("FILL-BIKE-" + i, "Honda", "Filler", Size.SMALL, "2020");
//            parkingService.park(new ParkingRequest(user102, filler));
//        }
        Vehicle overflowBike = new MotorCycle("MH15HJ0000", "Bajaj", "Overflow", Size.SMALL, "2020");
        Ticket overflowTicket = parkingService.park(new ParkingRequest(user102, overflowBike));
        System.out.println("SMALL was full, motorcycle landed in a "
                + overflowTicket.getSpot().getSpotSize() + " spot instead: "
                + overflowTicket.getSpot().getSpotId());

        // ---- UC5: Attempting to unpark an already-closed ticket ----
        section("UC5: Double-unpark the same ticket");
        parkingService.unpark(new UnparkingRequest(ticket2, user102)); // first unpark — succeeds
        try {
            parkingService.unpark(new UnparkingRequest(ticket2, user102)); // second attempt
        } catch (InvalidTicketException e) {
            System.out.println("Expected failure — ticket already closed: " + e.getMessage());
        }

        // ---- UC6: Paying via UPI instead of cash, proving dynamic strategy resolution ----
        section("UC6: Same ticket type, paid via UPI instead of cash");
        Ticket ticket4 = parkingService.park(new ParkingRequest(user103, user103.vehicle()));
        Fee upiFee = new Fee(BigDecimal.valueOf(60), Currency.getInstance("INR"));
        PaymentRequest upiRequest = new PaymentRequest(ticket4, new UPIDetails("avinash@Oksbi", "1234"));
        PaymentService upiPaymentService = new PaymentService(paymentStrategies.getOrThrow(PaymentMode.UPI));
        PaymentResponse upiResponse = upiPaymentService.pay(upiRequest);
        System.out.println("UPI payment result: " + upiResponse);

        parkingService.displayParking();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}