package com.shikavani.lld.snake_and_ladder.model;

import com.shikavani.lld.snake_and_ladder.exception.GameOverException;
import java.util.*;

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
        players.add(Objects.requireNonNull(player));
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public Board getBoard() {
        return board;
    }

    /** Read-only view of current turn order; safe to expose since it can't be mutated. */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(List.copyOf(players);
    }

    public Dice getDice() {
        return dice;
    }

    /**
     * Removes and returns the player whose turn it is.
     *
     * @throws GameOverException if called when {@link #isGameOver()} is already true
     */
    public Player nextPlayer(){
        if(isGameOver()){
            throw new GameOverException("Game is already over, Not enough players");
        }
        return players.poll();
    }

    /** Sends a player who just finished their turn to the back of the queue. */
    public void requeuePlayer(Player player) {
        players.offer(Objects.requireNonNull(player, "player must not be null"));
    }

    /**
     *  Active players in the game
     * @return total number of active players playing in the game.
     */
    public int getActivePlayerCount(){
        return this.players.size();
    }

    /** A game with fewer than 2 players left has nobody to play against. */
    public boolean isGameOver(){
        return this.players.size() < 2;
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
