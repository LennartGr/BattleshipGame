package com.battleship.client;

public enum HitStatus {
    
    NOT_ATTTACKED("cell not yet attacked"),
    MISSED("miss"),
    HIT("hit"),
    DESTROYED("ship on cell destroyed");

    private String textRepresentation;

    HitStatus(String textRepresentation) {
        this.textRepresentation = textRepresentation;
    }

    @Override
    public String toString() {
        return JansiHelper.colorize(textRepresentation, HitStatusColorizer.getColorString(this));
    }
}