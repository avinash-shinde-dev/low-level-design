package com.shikavani.lld.snake_and_ladder.strategy.overshoot;

public interface OverShootStrategy {
    int resolve(int currentPosition, int resolvedPosition, int boardSize);
}
