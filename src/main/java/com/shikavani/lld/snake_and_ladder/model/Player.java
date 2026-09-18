package com.shikavani.lld.snake_and_ladder.model;

public class Player {
    private final String playerId;
    private String name;
    private int position;

    public Player(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.position = 0;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
