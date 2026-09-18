package com.shikavani.lld.snake_and_ladder.model;

public record Ladder(int start, int end) {

    public Ladder{
        if (start <= 0 || end <= 0) {
            throw new IllegalArgumentException("Ladder start/end must be positive cell numbers");
        }
        if (start >= end) {
            throw new IllegalArgumentException("Ladder's start (base) must be less than end (top)");
        }
    }
}
