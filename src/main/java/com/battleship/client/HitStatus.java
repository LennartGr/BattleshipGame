package com.battleship.client;

/**
 * The HitStatus enum represents the possible hit statuses in a battleship game.
 * It provides a string representation of each hit status and colorizes the representation.
 */
public enum HitStatus {
    
    NOT_ATTTACKED("cell not yet attacked"),
    MISSED("miss"),
    HIT("hit"),
    DESTROYED("ship on cell destroyed");

    private String textRepresentation;

    /**
     * Constructs a HitStatus enum constant with the specified text representation.
     *
     * @param textRepresentation The text representation of the hit status.
     */
    HitStatus(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    /**
     * Returns the colorized string representation of the hit status.
     *
     * @return The colorized string representation.
     */
    @Override
    public String toString() {
        return JansiHelper.colorize(textRepresentation, HitStatusColorizer.getColorString(this));
    }
}
