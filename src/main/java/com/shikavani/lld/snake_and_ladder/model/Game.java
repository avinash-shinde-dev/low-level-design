package com.shikavani.lld.snake_and_ladder.model;

import com.shikavani.lld.snake_and_ladder.exception.GameOverException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Game {
    private final Board board;
    private final Queue<Player> players;
    private final Dice dice;
    public Game(Board board, Dice dice) {
        this.board = board;
        this.dice = dice;
        this.players = new ArrayDeque<>();
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public Board getBoard() {
        return board;
    }

    public Queue<Player> getPlayers() {
        return players;
    }

    public Dice getDice() {
        return dice;
    }

    public Player nextTurn(){
        if(players.size() <= 1){
            throw new GameOverException("Game is already over, Not enough players");
        }
        return players.poll();
    }

    public void reQueuePlayer(Player player){
        this.players.offer(player);
    }

    public int getActivePlayerCount(){
        return this.players.size();
    }

    @Override
    public String toString() {
        return "Game{" +
                "board=" + board +
                ", players=" + players +
                ", dice=" + dice +
                '}';
    }
}
