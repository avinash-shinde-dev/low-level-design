package com.shikavani.lld.librarymanagement.strategy.deliver;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.exception.DeliveryException;
import com.shikavani.lld.librarymanagement.notification.Notification;

public class EmailChannel implements DeliveryChannel {
    @Override
    public ChannelType type() {
        return ChannelType.EMAIL;
    }

    @Override
    public void deliver(Notification notification) throws DeliveryException {
        System.out.printf("Sending Email to Member : %s : %s", notification.recipient(), notification.message());

    }
}
