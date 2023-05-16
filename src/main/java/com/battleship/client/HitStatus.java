package com.battleship.client;

import java.io.Serializable;

public enum HitStatus implements Serializable {
    
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
        return textRepresentation;
    }
}