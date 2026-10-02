package com.shikavani.lld.librarymanagement.notification;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class InMemoryEventBus implements EventPublisher, EventSubscriber{
    private final List<Consumer<LibraryEvent>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void publish(LibraryEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        try{
            for(Consumer<LibraryEvent> listener: listeners){
                listener.accept(event);
            }
        }catch (RuntimeException exception){
            System.out.printf("Listener Failed : %s %s%n", event.getClass().getSimpleName(), exception);
        }
    }

    @Override
    public void subscribe(Consumer<LibraryEvent> listener) {
        listeners.add(Objects.requireNonNull(listener, "Listener must not be null"));
    }
}
