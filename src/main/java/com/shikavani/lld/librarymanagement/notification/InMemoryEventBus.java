package com.shikavani.lld.librarymanagement.notification;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Tiny event bus. A listener that fails never stops the other listeners or the caller. */
public class InMemoryEventBus implements EventPublisher, EventSubscriber {
    private final List<Consumer<LibraryEvent>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void publish(LibraryEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        for (Consumer<LibraryEvent> listener : listeners) {
            try {
                listener.accept(event);
            } catch (RuntimeException e) {       // try/catch is PER listener
                System.out.println("Listener failed for " + event.getClass().getSimpleName() + ": " + e);
            }
        }
    }

    @Override
    public void subscribe(Consumer<LibraryEvent> listener) {
        listeners.add(Objects.requireNonNull(listener, "Listener must not be null"));
    }
}
