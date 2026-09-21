package com.shikavani.lld.vendingmachine.registry;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class StrategyRegistry<K, V> {
    private final Map<K, V> registries;

    private StrategyRegistry(Map<K, V> registry){
        registries = registry;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public Optional<V> find(K key){
        return Optional.ofNullable(registries.get(key));
    }

    public V getOrThrow(K key) {
        return find(key)
                .orElseThrow(() -> new IllegalArgumentException("No strategy registered for: " + key));
    }

    public static final class Builder<K, V> {
        private final Map<K, V> entries = new ConcurrentHashMap<>();

        public Builder<K, V> register(K key, V value){
            this.entries.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
            return this;
        }

        public StrategyRegistry<K,V> build() {
            return new StrategyRegistry(Map.copyOf(entries));
        }
    }
}
