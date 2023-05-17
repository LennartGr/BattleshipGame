package com.battleship.events;

// TODO delete, obsolete 

public class StartMessageEvent extends Event {
    
    final boolean starting;

    public StartMessageEvent(boolean starting) {
        super();
        this.starting = starting;
    }

    public boolean getStarting() {
        return this.starting;
    }

    public String toString() {
        return starting ? "Game started. You may attack first." : "Game started. The other player attacks first.";
    }
}
