package com.shikavani.lld.snake_and_ladder.model;

public record Snake(int start, int end) {
    public Snake {
        if(start < end) {
            throw new IllegalArgumentException("Snake's start i.e head should be greater than end i.e tail");
        }
    }
}
