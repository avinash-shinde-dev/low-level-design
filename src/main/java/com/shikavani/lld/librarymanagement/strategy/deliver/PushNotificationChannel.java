package com.shikavani.lld.librarymanagement.strategy.deliver;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.notification.Notification;

public class PushNotificationChannel implements DeliveryChannel{

    @Override
    public ChannelType type() {
        return ChannelType.PUSH;
    }

    @Override
    public void deliver(Notification notification) throws DeliveryException {
        System.out.printf("Sending Push Notification to Member : %s : %s", notification.recipient(), notification.message());
    }
}
