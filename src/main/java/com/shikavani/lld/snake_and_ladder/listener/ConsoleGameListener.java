package com.shikavani.lld.snake_and_ladder.listener;

import com.shikavani.lld.snake_and_ladder.model.Player;

public class ConsoleGameListener implements GameListener{
    @Override
    public void onDiceRolled(Player player, int steps) {
        System.out.printf("%s rolled a %d%n", player.getName(), steps);
    }

    @Override
    public void onPlayerMoved(Player player, int fromPosition, int toPosition) {
        System.out.printf("%s moved from %d to %d%n", player.getName(), fromPosition, toPosition);

    }

    @Override
    public void onSnakeBite(Player player, int head, int tail) {
        System.out.printf("%s bitten by a snake at %d, slides down to %d%n", player.getName(), head, tail);
    }

    @Override
    public void onLadderClimb(Player player, int base, int top) {
        System.out.printf("%s found a ladder at %d, climbs to %d%n", player.getName(), base, top);
    }

    @Override
    public void onOverShoot(Player player, int attemptedPosition) {
        System.out.printf("%s rolled past the board (%d), stays at %d%n",
                player.getName(), attemptedPosition, player.getPosition());
    }

    @Override
    public void onGameWon(Player player) {
        System.out.printf("Congratulations %s, you won!%n", player.getName());
    }
}
