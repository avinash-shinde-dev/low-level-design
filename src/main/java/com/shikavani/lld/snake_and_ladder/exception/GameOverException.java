package com.shikavani.lld.snake_and_ladder.exception;

/**
 * Thrown when a caller asks {@code Game} to advance the turn after the game has
 * already ended. Callers are expected to check {@code Game.isGameOver()} first
 * (see {@code GameService.start()}) - this exception is a programmer-error guard,
 * not the normal way a game ends.
 */
public class GameOverException extends RuntimeException{
    public GameOverException(String message) {
        super(message);
    }
}
