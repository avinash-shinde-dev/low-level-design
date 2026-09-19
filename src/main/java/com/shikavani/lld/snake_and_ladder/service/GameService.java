package com.shikavani.lld.snake_and_ladder.service;

import com.shikavani.lld.snake_and_ladder.listener.GameListener;
import com.shikavani.lld.snake_and_ladder.model.Board;
import com.shikavani.lld.snake_and_ladder.model.Game;
import com.shikavani.lld.snake_and_ladder.model.MoveOutcome;
import com.shikavani.lld.snake_and_ladder.model.Player;
import com.shikavani.lld.snake_and_ladder.strategy.overshoot.OverShootStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.win.WinningStrategy;

import java.util.Objects;

public class GameService {
    private final Game game;
    private final WinningStrategy winningStrategy;
    private final OverShootStrategy overShootStrategy;
    private final GameListener listener;

    public GameService(Game game, WinningStrategy winningStrategy, OverShootStrategy overShootStrategy, GameListener listener) {
        this.game = Objects.requireNonNull(game, "Game must not be null");
        this.winningStrategy = Objects.requireNonNull(winningStrategy, "Winning Strategy must not be null");
        this.overShootStrategy = Objects.requireNonNull(overShootStrategy, "Overshoot Strategy must not be null");
        this.listener = Objects.requireNonNull(listener, "Listener must not be null");
    }

    public void start() {

        while(!this.game.isGameOver()){
            Player player = this.game.nextPlayer();
            playTurn(player);

            if(this.winningStrategy.win(this.game.getBoard(), player.getPosition())){
                System.out.printf("Congratulations %s", player.getName());
                break;
            }
            this.game.requeuePlayer(player);
        }
    }

    private void playTurn(Player player) {
        Board board = this.game.getBoard();

        int steps = this.game.getDice().roll();
        listener.onDiceRolled(player, steps);

        int fromPosition = player.getPosition();
        MoveOutcome outcome = this.game.getBoard().resolvePosition(fromPosition, steps);

        int finalPosition = this.overShootStrategy.resolve(fromPosition, outcome.resolvePosition(), board.getSize());

        if(finalPosition == fromPosition && outcome.rawPosition() > board.getSize() ){
            listener.onOverShoot(player, outcome.rawPosition());
            return;
        }
        switch (outcome.event()){
            case NONE -> listener.onPlayerMoved(player, outcome.fromPosition(), outcome.resolvePosition());
            case SNAKE_BITE -> listener.onSnakeBite(player, outcome.rawPosition(), outcome.resolvePosition());
            case LADDER_CLIMB -> listener.onLadderClimb(player, outcome.rawPosition(), outcome.resolvePosition());
        }
        player.setPosition(finalPosition);
    }


}
