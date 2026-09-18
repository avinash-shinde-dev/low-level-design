package com.shikavani.lld.snake_and_ladder.strategy;

import com.shikavani.lld.snake_and_ladder.model.Board;

public interface WinningStrategy {
    boolean win(Board board, int newPosition);
}
