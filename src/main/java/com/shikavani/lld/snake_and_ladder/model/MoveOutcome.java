package com.shikavani.lld.snake_and_ladder.model;

import com.shikavani.lld.snake_and_ladder.enums.MoveEvent;

/**
 * What happened when a move was resolved against the board.
 */
public record MoveOutcome(int fromPosition, int rawPosition, int resolvePosition, MoveEvent event) { }
