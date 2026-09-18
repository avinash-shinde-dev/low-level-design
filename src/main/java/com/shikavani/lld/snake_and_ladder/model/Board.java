package com.shikavani.lld.snake_and_ladder.model;

import java.util.List;

public record Board(int size, List<Snake> snakes, List<Ladder> ladders) {

    @Override
    public List<Snake> snakes() {
        return List.copyOf(snakes);
    }

    @Override
    public List<Ladder> ladders() {
        return List.copyOf(ladders);
    }

    public void addSnake(int start, int end) {
        validate(start, "Start");
        validate(end, "End");
        this.snakes.add(new Snake(start, end));
    }

    public void addLadder(int start, int end) {
        validate(start, "Start");
        validate(end, "End");
        this.ladders.add(new Ladder(start, end));
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

    private void validate(int value, String type) {
        if (value < 1 || value > size) {
            throw new IllegalArgumentException(String.format("%s value must be within range [ %s , %s", type, 1, size));
        }
    }
}
