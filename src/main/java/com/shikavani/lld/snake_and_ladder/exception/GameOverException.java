package com.shikavani.lld.snake_and_ladder.exception;

public class GameOverException extends RuntimeException{
    public GameOverException(String message) {
        super(message);
    }
}
