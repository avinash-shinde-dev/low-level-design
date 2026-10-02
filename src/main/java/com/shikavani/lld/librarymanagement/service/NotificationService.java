package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.enums.EventType;
import com.shikavani.lld.librarymanagement.exception.DeliveryException;
import com.shikavani.lld.librarymanagement.notification.EventSubscriber;
import com.shikavani.lld.librarymanagement.notification.LibraryEvent;
import com.shikavani.lld.librarymanagement.notification.Notification;
import com.shikavani.lld.librarymanagement.strategy.deliver.DeliveryChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotificationService {

    private final Map<ChannelType, DeliveryChannel> channels;
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public NotificationService(List<DeliveryChannel> channels, EventSubscriber bus) {
        this.channels = channels.stream().collect(Collectors.toMap(DeliveryChannel::type, Function.identity()));
        bus.subscribe(this::handle);
    }

    public void handle(LibraryEvent event){
        Notification notification = getNotification(event);
        for(ChannelType channelType: notification.recipient().preferredNotificationChannel()){
            DeliveryChannel channel = this.channels.get(channelType);

            if(channel == null){
                System.out.printf("No Delivery channel registered for channel type: %s", channelType);
                continue;
            }

            executorService.submit(() -> {
                try {
                    this.channels.get(channelType).deliver(notification);
                }catch(DeliveryException exception){
                    System.out.printf("Delivery Failed: %s", exception);
                }
            });
        }
    }

    private Notification getNotification(LibraryEvent event){
        return switch (event) {
            case LibraryEvent.DueSoon d -> new Notification(d.member(), EventType.DUE_DATE_REMINDER, String.format("Due date : %s for bookCopyId: %s is near", d.transaction().dueAt(), d.transaction().bookCopyId()));
            case LibraryEvent.OverDue o -> new Notification(o.member(), EventType.OVERDUE, String.format("OverDue of Fine: %s ", o.transaction().fineBreakdown()));
            case LibraryEvent.HoldReady r -> new Notification(r.member(), EventType.HOLD_AVAILABLE, String.format("Hold available for member: %s with hold: %s", r.hold().getMemberId(), r.hold().getHoldId()));
            case LibraryEvent.HoldExpired e -> new Notification(e.member(), EventType.HOLD_EXPIRED, String.format("Hold : %s is expired, Notifying member : %s", e.hold().getHoldId(), e.hold().getMemberId()));
        };
    }
}
