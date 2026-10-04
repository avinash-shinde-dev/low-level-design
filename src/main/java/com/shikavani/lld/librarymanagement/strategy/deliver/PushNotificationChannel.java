package com.shikavani.lld.librarymanagement.strategy.deliver;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.exception.DeliveryException;
import com.shikavani.lld.librarymanagement.notification.Notification;

/** Pretend delivery: just prints. A real one would call an email / SMS / push provider. */
public class PushNotificationChannel implements DeliveryChannel {
    @Override public ChannelType type() { return ChannelType.PUSH; }

    @Override
    public void deliver(Notification n) throws DeliveryException {
        System.out.printf("[Push] to %s: %s%n", n.recipient().getName(), n.message());
    }
}
