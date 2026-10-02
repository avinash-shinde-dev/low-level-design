package com.shikavani.lld.librarymanagement.notification;

import java.util.function.Consumer;

public interface EventPublisher {
    void publish(LibraryEvent events);
}
