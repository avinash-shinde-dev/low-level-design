package com.shikavani.lld.snake_and_ladder.model;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {
    private final int sides;
    public Dice(int sides) {
       this.sides = sides;
    }

    public int getSides() {
        return sides;
    }

    /**
     * ThreadLocalRandom.current() avoids contention/thread-safety issues you'd get
     * from a single shared Random instance if multiple threads roll dice concurrently.
     * @return random integer.
     */
    public int roll(){
        return ThreadLocalRandom.current().nextInt(1, sides+1);
    }
}
