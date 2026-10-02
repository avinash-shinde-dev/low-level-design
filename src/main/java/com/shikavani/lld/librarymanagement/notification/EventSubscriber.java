package com.shikavani.lld.librarymanagement.notification;

import java.util.function.Consumer;

public interface EventSubscriber {
    void subscribe(Consumer<LibraryEvent> listener);
}
