package com.battleship.client;

/**
 * The Ship class represents a ship in a battleship game.
 * It provides methods for accessing ship properties.
 */
public class Ship {

    private final int id;
    private final int length;
    private final String symbol;

    /**
     * Constructs a Ship object with the specified ID, length, and symbol.
     *
     * @param id     The ID of the ship.
     * @param length The length of the ship.
     * @param symbol The symbol representing the ship.
     */
    public Ship(int id, int length, String symbol) {
        this.id = id;
        this.length = length;
        this.symbol = symbol;
    }

    /**
     * Retrieves the ID of the ship.
     *
     * @return The ID of the ship.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the length of the ship.
     *
     * @return The length of the ship.
     */
    public int getLength() {
        return length;
    }

    /**
     * Retrieves the symbol representing the ship.
     *
     * @return The symbol representing the ship.
     */
    public String getSymbol() {
        return symbol;
    }
}
