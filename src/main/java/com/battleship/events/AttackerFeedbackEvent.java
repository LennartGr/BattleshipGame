package com.battleship.events;

import java.io.Serializable;

import com.battleship.client.BattleshipException;
import com.battleship.client.HitStatus;

/**
 * Event class representing the feedback from an attacker's attack.
 */
public record AttackerFeedbackEvent(boolean attackSuccess, HitStatus hitStatus, BattleshipException exception)
        implements Serializable {
}
