package com.shikavani.lld.parkinglot.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class StrategyRegistry<K, V> {

    private final Map<K, V> strategies;

    private StrategyRegistry(Map<K, V> strategies) {
        this.strategies = strategies;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public Optional<V> find(K key) {
        return Optional.ofNullable(strategies.get(key));
    }

    public V getOrThrow(K key) {
        return find(key)
                .orElseThrow(() -> new IllegalArgumentException("No strategy registered for: " + key));
    }

    public static final class Builder<K, V> {

        private final Map<K, V> entries = new HashMap<>();

        public Builder<K, V> register(K key, V value) {
            entries.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return this;
        }

        public StrategyRegistry<K, V> build() {
            return new StrategyRegistry<>(Map.copyOf(entries)); // unmodifiable + safe to share across threads
        }
    }
}