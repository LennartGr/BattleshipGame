package com.battleship.client;

public class Ship {
    
    private final int id;
    private final int length;
    private final String symbol;

    public Ship(int id, int length, String symbol) {
        this.id = id;
        this.length = length;
        this.symbol = symbol;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public String getSymbol() {
        return symbol;
    }
}
