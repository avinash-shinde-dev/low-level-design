package com.shikavani.lld.snake_and_ladder.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Board {

    private final int size;
    private final List<Snake> snakes;
    private final List<Ladder> ladders;
    private final Map<Integer, Integer> snakeHeadToTail;
    private final Map<Integer, Integer> ladderBaseToTop;

    public Board(Builder builder){
        this.size = builder.size;
        this.snakes = List.copyOf(builder.snakes);
        this.ladders = List.copyOf(builder.ladders);

        Map<Integer, Integer> snakeMap = new HashMap<>();
        for (Snake snake : this.snakes) {
            snakeMap.put(snake.start(), snake.end());
        }
        this.snakeHeadToTail = Map.copyOf(snakeMap);

        Map<Integer, Integer> ladderMap = new HashMap<>();
        for (Ladder ladder : this.ladders) {
            ladderMap.put(ladder.start(), ladder.end());
        }
        this.ladderBaseToTop = Map.copyOf(ladderMap);

    }

    public static Builder builder(int size) {
        return  new Builder(size);
    }

    public int getSize() {
        return size;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public int movePosition(Player player, int steps){
        int newPosition = player.getPosition() + steps;
        for (Snake snake: this.snakes) {
            if(newPosition == snake.start()){
                System.out.printf("Player %s eaten by snake, and moved to %s", player.getName(), snake.end());
               return snake.end();
            }
        }

        for (Ladder ladder: this.ladders) {
            if(newPosition == ladder.start()){
                System.out.printf("Player %s got the ladder, and moved to %s", player.getName(), ladder.end() );
                return ladder.end();
            }
        }
        System.out.printf("Player %s moved to %s", player.getName(), newPosition);
        return newPosition;
    }

    public static final class Builder {
        private final int size;
        private final List<Snake> snakes = new ArrayList<>();
        private final List<Ladder> ladders = new ArrayList<>();

        private Builder(int size) {
            if (size <= 0) {
                throw new IllegalArgumentException("Board size must be positive");
            }
            this.size = size;
        }

        public Builder addSnake(int start, int end) {
            validateWithinBounds(start, "Snake Start");
            validateWithinBounds(end, "Snake End");
            this.snakes.add(new Snake(start, end));
            return this;
        }

        public Builder addLadder(int start, int end) {
            validateWithinBounds(start, "Ladder Start");
            validateWithinBounds(end, "Ladder End");
            this.ladders.add(new Ladder(start, end));
            return this;
        }

        public Board build(){
             // validateNoDuplicateOrCyclicStarts();
             return  new Board(this);
        }

        private void validateWithinBounds(int value, String label) {
            if (value < 1 || value > size) {
                throw new IllegalArgumentException(
                        "%s value must be within range [1, %d], got %d".formatted(label, size, value));
            }
        }

    }
}
