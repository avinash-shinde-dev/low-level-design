package com.shikavani.lld.snake_and_ladder.strategy.winning;

import com.shikavani.lld.snake_and_ladder.model.Board;

public class FirstOneToCrossWinningStrategy implements WinningStrategy {
    @Override
    public boolean win(Board board, int newPosition) {
        return newPosition >= board.getSize();
    }
}
