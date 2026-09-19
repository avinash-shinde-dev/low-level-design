package com.shikavani.lld.snake_and_ladder;

import com.shikavani.lld.snake_and_ladder.listener.ConsoleGameListener;
import com.shikavani.lld.snake_and_ladder.listener.GameListener;
import com.shikavani.lld.snake_and_ladder.model.*;
import com.shikavani.lld.snake_and_ladder.service.GameService;
import com.shikavani.lld.snake_and_ladder.strategy.overshoot.OverShootStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.overshoot.StayInPlaceOverShootStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.win.ExactWinningStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.win.WinningStrategy;
import java.util.Random;
import java.util.UUID;

public class SnakeAndLadderGameApp {
    private static final Integer BOARD_SIZE = 100;
    public static void main(String[] args) {
        Board board = getBoard();
        Dice dice = new NormalDice();
        Game game = new Game(board, dice);

        Player player1 = new Player(UUID.randomUUID().toString(), "Avinash");
        Player player2 = new Player(UUID.randomUUID().toString(), "Dnyaneshwari");
        game.addPlayer(player1);
        game.addPlayer(player2);
        WinningStrategy winningStrategy = new ExactWinningStrategy();
        OverShootStrategy overShootStrategy = new StayInPlaceOverShootStrategy();
        GameListener listener = new ConsoleGameListener();
        GameService gameService = new GameService(game, winningStrategy, overShootStrategy, listener);
       // game.removePlayer(player1);
        gameService.start();

    }

    private static Board getBoard() {
        Board.Builder builder = Board.builder(BOARD_SIZE);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int start = random.nextInt(1, BOARD_SIZE);
            int end = random.nextInt(1, BOARD_SIZE);
            if(start > end)
                builder.addSnake(start, end);
        }

        for (int i = 0; i < 5; i++) {
            int start = random.nextInt(1, BOARD_SIZE);
            int end = random.nextInt(1, BOARD_SIZE);
            if(start < end)
                builder.addLadder(start, end);
        }

        Board board = builder.build();
        return board;
    }
}
