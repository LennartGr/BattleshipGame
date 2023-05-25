package com.battleship.events;

import java.io.Serializable;

import com.battleship.client.Coordinates;
import com.battleship.client.HitStatus;

/**
 * Event class representing the feedback for the defender after being attacked.
 */
public record DefenderFeedbackEvent(Coordinates coordinates, HitStatus hitStatus) implements Serializable {

    /**
     * Returns a string representation of the DefenderFeedbackEvent.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "";
    }
}
