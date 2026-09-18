package com.shikavani.lld.snake_and_ladder;

import com.shikavani.lld.snake_and_ladder.model.*;
import com.shikavani.lld.snake_and_ladder.service.GameService;
import com.shikavani.lld.snake_and_ladder.strategy.ExactWinningStrategy;
import com.shikavani.lld.snake_and_ladder.strategy.WinningStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SnakeAndLadderGameApp {
    private static final Integer BOARD_SIZE = 100;
    public static void main(String[] args) {
        Random random = new Random();
        List<Snake> snakes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int start = random.nextInt(1, BOARD_SIZE);
            int end = random.nextInt(1, BOARD_SIZE);
            if(start > end){
                snakes.add(new Snake(start, end));
            }
        }
        List<Ladder> ladders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int start = random.nextInt(1, BOARD_SIZE);
            int end = random.nextInt(1, BOARD_SIZE);
            if(start < end){
                ladders.add(new Ladder(start, end));
            }
        }

        Board board = new Board(BOARD_SIZE, snakes, ladders);

        Dice dice = new NormalDice();
        Game game = new Game(board, dice);
        Player player1 = new Player(UUID.randomUUID().toString(), "Avinash");
        Player player2 = new Player(UUID.randomUUID().toString(), "Dnyaneshwari");
        game.addPlayer(player1);
        game.addPlayer(player2);
        WinningStrategy winningStrategy = new ExactWinningStrategy();

        GameService gameService = new GameService(game, winningStrategy);
       // game.removePlayer(player1);
        gameService.start();

    }
}
