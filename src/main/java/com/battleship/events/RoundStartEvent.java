package com.battleship.events;

import java.io.Serializable;

public record RoundStartEvent(GameStatus gameStatus, boolean attacking) implements Serializable {
   
    public enum GameStatus {
        GAME_ON,
        YOU_LOST,
        YOU_WON
    }

    @Override
    public String toString() {
        return attacking ? "You may attack." : "The other player is attacking.";
    }
}
