package com.shikavani.lld.snake_and_ladder.model;

import com.shikavani.lld.snake_and_ladder.enums.MoveEvent;
import java.util.*;

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

    public MoveOutcome resolvePosition(int currentPosition, int steps){
        int rawPosition = currentPosition + steps;

        if(rawPosition > size){
            return new MoveOutcome(currentPosition, rawPosition, rawPosition, MoveEvent.NONE);
        }

        Integer snakeTail = snakeHeadToTail.get(rawPosition);
        if(snakeTail != null){
            return new MoveOutcome(currentPosition, rawPosition, snakeTail, MoveEvent.SNAKE_BITE);
        }

        Integer ladderTop = ladderBaseToTop.get(rawPosition);
        if(ladderTop != null){
            return new MoveOutcome(currentPosition, rawPosition, ladderTop, MoveEvent.LADDER_CLIMB);
        }

        return new MoveOutcome(currentPosition, rawPosition, rawPosition, MoveEvent.NONE);
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
             validateNoDuplicateOrCyclicStarts();
             return  new Board(this);
        }

        private void validateNoDuplicateOrCyclicStarts() {
            Set<Integer> starts = new HashSet<>();
            for (Snake snake : snakes) {
                if (!starts.add(snake.start())) {
                    throw new IllegalArgumentException("Duplicate snake/ladder start cell: " + snake.start());
                }
            }
            for (Ladder ladder : ladders) {
                if (!starts.add(ladder.start())) {
                    throw new IllegalArgumentException("Duplicate snake/ladder start cell: " + ladder.start());
                }
            }

            Map<Integer, Integer> combined = new HashMap<>();
            for (Snake snake : snakes) {
                combined.put(snake.start(), snake.end());
            }
            for (Ladder ladder : ladders) {
                combined.put(ladder.start(), ladder.end());
            }

            for (Map.Entry<Integer, Integer> entry : combined.entrySet()) {
                if (combined.containsKey(entry.getValue()) && combined.get(entry.getValue()).equals(entry.getKey())) {
                    throw new IllegalArgumentException(
                            "Cycle detected between cells " + entry.getKey() + " and " + entry.getValue());
                }
            }
        }


        private void validateWithinBounds(int value, String label) {
            if (value < 1 || value > size) {
                throw new IllegalArgumentException(
                        "%s value must be within range [1, %d], got %d".formatted(label, size, value));
            }
        }

    }
}
