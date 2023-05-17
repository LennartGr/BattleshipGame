package com.battleship.events;

import java.io.Serializable;

public record RoundStartEvent(GameStatus gameStatus, AttackStatus attackStatus) implements Serializable {
   
    public enum GameStatus {
        GAME_ON,
        YOU_LOST,
        YOU_WON
    }

    public enum AttackStatus {
        ATTACK,
        ATTACK_AGAIN, 
        DEFEND,
        DEFEND_AGAIN,
    }

    @Override
    public String toString() {
        switch (attackStatus) {
            case ATTACK: return "You may attack.";
            case ATTACK_AGAIN: return "You may attack again.";
            case DEFEND: return "The other player is attacking.";
            case DEFEND_AGAIN: return "The other player may attack once more.";
            default: return "";
        }
    }
}
