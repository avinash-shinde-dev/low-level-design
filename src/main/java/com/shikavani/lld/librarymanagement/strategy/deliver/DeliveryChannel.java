package com.shikavani.lld.librarymanagement.strategy.deliver;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.exception.DeliveryException;
import com.shikavani.lld.librarymanagement.notification.Notification;

/** A way to reach a member. To add a channel, write a new class and pass it to NotificationService. */
public interface DeliveryChannel {
    ChannelType type();
    void deliver(Notification notification) throws DeliveryException;
}
