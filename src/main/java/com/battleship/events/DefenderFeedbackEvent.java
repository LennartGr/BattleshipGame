package com.battleship.events;

import java.io.Serializable;

import com.battleship.client.Coordinates;
import com.battleship.client.HitStatus;

public record DefenderFeedbackEvent(Coordinates coordinates, HitStatus hitStatus) implements Serializable {
    
    @Override
    public String toString() {
        return "";
    }
}
