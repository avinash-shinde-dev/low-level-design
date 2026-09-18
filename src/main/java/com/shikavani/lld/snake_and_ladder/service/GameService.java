package com.shikavani.lld.snake_and_ladder.service;

import com.shikavani.lld.snake_and_ladder.exception.GameOverException;
import com.shikavani.lld.snake_and_ladder.model.Game;
import com.shikavani.lld.snake_and_ladder.model.Player;
import com.shikavani.lld.snake_and_ladder.strategy.ExactWinningStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.WinningStrategy;

public class GameService {
    private final Game game;
    private final WinningStrategy winningStrategy;

    public GameService(Game game, WinningStrategy winningStrategy) {
        this.game = game;
        this.winningStrategy = winningStrategy;
    }

    public void start() {
        if(this.game.getPlayers().size() <= 1){
            throw new GameOverException("Not enough players.");
        }
        while(!this.game.getPlayers().isEmpty()){
            Player player = this.game.nextTurn();
            playTurn(player);
            this.game.reQueuePlayer(player);
            if(this.winningStrategy.win(this.game.getBoard(), player.getPosition())){
                System.out.printf("Congratulations %s", player.getName());
                break;
            }
        }
    }

    private void playTurn(Player player) {
        int steps = this.game.getDice().roll();
        int newPosition = this.game.getBoard().movePosition(player, steps);

        if(!(this.winningStrategy instanceof ExactWinningStrategy && newPosition > this.game.getBoard().size())){
           player.setPosition(newPosition);
        }
    }


}
