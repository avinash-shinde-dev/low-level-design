package com.shikavani.lld.snake_and_ladder.model;

public record Snake(int start, int end) {
    public Snake {
        if (start <= 0 || end <= 0) {
            throw new IllegalArgumentException("Snake start/end must be positive cell numbers");
        }
        if (start <= end) {
            throw new IllegalArgumentException("Snake's start (head) must be greater than end (tail)");
        }
    }
}
