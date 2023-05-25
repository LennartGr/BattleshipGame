package com.battleship.events;

import java.io.Serializable;

/**
 * Event class representing the start of a round.
 */
public record RoundStartEvent(GameStatus gameStatus, AttackStatus attackStatus) implements Serializable {

    /**
     * Enum representing the game status.
     */
    public enum GameStatus {
        GAME_ON,
        YOU_LOST,
        YOU_WON
    }

    /**
     * Enum representing the attack status.
     */
    public enum AttackStatus {
        ATTACK,
        ATTACK_AGAIN,
        DEFEND,
        DEFEND_AGAIN,
    }

    /**
     * Returns a string representation of the RoundStartEvent.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        switch (attackStatus) {
            case ATTACK:
                return "You may attack.";
            case ATTACK_AGAIN:
                return "You may attack again.";
            case DEFEND:
                return "The other player is attacking.";
            case DEFEND_AGAIN:
                return "The other player may attack once more.";
            default:
                return "";
        }
    }
}
