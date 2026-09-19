package com.shikavani.lld.snake_and_ladder.listener;

import com.shikavani.lld.snake_and_ladder.model.Player;

public interface GameListener {

    void onDiceRolled(Player player, int steps);

    void onPlayerMoved(Player player, int fromPosition, int toPosition);

    void onSnakeBite(Player player, int head, int tail);

    void onLadderClimb(Player player, int base, int top);

    void onOverShoot(Player player, int attemptedPosition);

    void onGameWon(Player player);

}
