package com.shikavani.lld.snake_and_ladder.model;

import java.util.concurrent.ThreadLocalRandom;

public final class NormalDice implements Dice{

    private static final int SIDE = 6;

    @Override
    public int getSides() {
        return SIDE;
    }

    /**
     * ThreadLocalRandom.current() avoids contention/thread-safety issues you'd get
     * from a single shared Random instance if multiple threads roll dice concurrently.
     * @return random integer.
     */
    @Override
    public int roll() {
        return ThreadLocalRandom.current().nextInt(1, SIDE+1);
    }
}
