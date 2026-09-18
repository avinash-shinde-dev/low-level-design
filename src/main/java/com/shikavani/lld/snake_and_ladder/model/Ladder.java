package com.shikavani.lld.snake_and_ladder.model;

public record Ladder(int start, int end) {

    public Ladder{
        if(start > end){
            throw new IllegalArgumentException("Ladder start i.e base should be less than end i.e top");
        }
    }
}
