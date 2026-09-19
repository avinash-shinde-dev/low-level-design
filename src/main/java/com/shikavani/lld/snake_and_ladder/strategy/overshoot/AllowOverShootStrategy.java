package com.shikavani.lld.snake_and_ladder.strategy.overshoot;

public class AllowOverShootStrategy implements OverShootStrategy{
    @Override
    public int resolve(int currentPosition, int resolvedPosition, int boardSize) {
        return resolvedPosition;
    }
}
