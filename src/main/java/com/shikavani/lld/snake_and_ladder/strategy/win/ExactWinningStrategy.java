package com.shikavani.lld.snake_and_ladder.strategy.win;

import com.shikavani.lld.snake_and_ladder.model.Board;

public class ExactWinningStrategy implements WinningStrategy {

    @Override
    public boolean win(Board board, int newPosition) {
        return board.getSize() == newPosition;
    }
}
