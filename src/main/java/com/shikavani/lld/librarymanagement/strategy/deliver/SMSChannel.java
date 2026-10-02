package com.shikavani.lld.librarymanagement.strategy.deliver;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.exception.DeliveryException;
import com.shikavani.lld.librarymanagement.notification.Notification;

public class SMSChannel implements DeliveryChannel {

    @Override
    public ChannelType type() {
        return ChannelType.SMS;
    }

    @Override
    public void deliver(Notification notification) throws DeliveryException {
        System.out.printf("Sending SMS to Member : %s : %s", notification.recipient(), notification.message());
    }
}
