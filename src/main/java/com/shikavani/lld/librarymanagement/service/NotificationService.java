package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.enums.EventType;
import com.shikavani.lld.librarymanagement.notification.EventSubscriber;
import com.shikavani.lld.librarymanagement.notification.LibraryEvent;
import com.shikavani.lld.librarymanagement.notification.Notification;
import com.shikavani.lld.librarymanagement.strategy.deliver.DeliveryChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Listens to library events, turns them into notifications and sends them on each channel the
 * member prefers. A channel that fails is logged and skipped: it can never break borrow / return.
 */
public class NotificationService {
    private final Map<ChannelType, DeliveryChannel> channels;
    private final Executor executor;

    /** Normal use: delivery runs in the background (virtual threads). */
    public NotificationService(List<DeliveryChannel> channels, EventSubscriber bus) {
        this(channels, bus, Executors.newVirtualThreadPerTaskExecutor());
    }

    /** Tests can pass "Runnable::run" to deliver immediately on the calling thread. */
    public NotificationService(List<DeliveryChannel> channels, EventSubscriber bus, Executor executor) {
        this.channels = channels.stream().collect(Collectors.toMap(DeliveryChannel::type, Function.identity()));
        this.executor = executor;
        bus.subscribe(this::handle);
    }

    public void handle(LibraryEvent event) {
        Notification notification = toNotification(event);
        for (ChannelType type : notification.recipient().preferredNotificationChannels()) {
            DeliveryChannel channel = channels.get(type);
            if (channel == null) {
                System.out.println("No delivery channel registered for " + type);
                continue;
            }
            executor.execute(() -> {
                try {
                    channel.deliver(notification);
                } catch (RuntimeException e) {       // any failure of one channel stays inside this block
                    System.out.println("Delivery failed on " + type + ": " + e.getMessage());
                }
            });
        }
    }

    private Notification toNotification(LibraryEvent event) {
        return switch (event) {
            case LibraryEvent.DueSoon e -> new Notification(e.member(), EventType.DUE_DATE_REMINDER,
                    "Reminder: your book is due on " + e.transaction().dueAt());
            case LibraryEvent.OverDue e -> new Notification(e.member(), EventType.OVERDUE,
                    "Your book is overdue. Fine so far: " + e.fineSoFar().total());
            case LibraryEvent.HoldReady e -> new Notification(e.member(), EventType.HOLD_AVAILABLE,
                    "Your reserved book is ready. Please pick it up before " + e.hold().getPickupDeadline());
            case LibraryEvent.HoldExpired e -> new Notification(e.member(), EventType.HOLD_EXPIRED,
                    "Your hold expired because the book was not picked up in time");
        };
    }
}
