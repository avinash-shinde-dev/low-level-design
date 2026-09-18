package com.shikavani.lld.snake_and_ladder.model;

import java.util.Objects;

public class Player {
    private final String playerId;
    private String name;
    private int position;

    public Player(String playerId, String name) {
        this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
        this.name = requireNonBlank(name, "name");
        this.position = 0;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonBlank(name, "name");;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if(position < 1 ){
            throw new IllegalArgumentException("Position cannot be less than 1");
        }
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
