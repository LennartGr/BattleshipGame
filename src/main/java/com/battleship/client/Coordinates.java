package com.battleship.client;

import java.io.Serializable;

/**
 * The Coordinates class represents a pair of coordinates on a grid.
 * It provides methods for creating and accessing coordinates.
 */
public class Coordinates implements Serializable {

    private final char textX;
    private final int x;
    private final int y;

    /**
     * Constructs a Coordinates object using a character representation of the X-coordinate
     * and an integer representation of the Y-coordinate.
     *
     * @param textX   The character representation of the X-coordinate.
     * @param numberY The integer representation of the Y-coordinate.
     */
    public Coordinates(char textX, int numberY) {
        this.textX = textX;
        this.x = charToInt(textX);
        this.y = numberY;
    }

    /**
     * Constructs a Coordinates object using integer representations of the X-coordinate and Y-coordinate.
     *
     * @param x The integer representation of the X-coordinate.
     * @param y The integer representation of the Y-coordinate.
     */
    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        this.textX = (char) (x + 65);
    }

    /**
     * Retrieves the integer representation of the X-coordinate.
     *
     * @return The integer representation of the X-coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Retrieves the character representation of the X-coordinate.
     *
     * @return The character representation of the X-coordinate.
     */
    public char getXtext() {
        return this.textX;
    }

    /**
     * Retrieves the integer representation of the Y-coordinate.
     *
     * @return The integer representation of the Y-coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Converts a character to its corresponding integer representation.
     *
     * @param c The character to convert.
     * @return The integer representation of the character.
     * @throws IllegalArgumentException if the input character is not a letter.
     */
    public static int charToInt(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else {
            throw new IllegalArgumentException("Input character is not a letter.");
        }
    }

    /**
     * Returns a string representation of the Coordinates object.
     * The string consists of the X-coordinate followed by the Y-coordinate.
     *
     * @return The string representation of the Coordinates object.
     */
    @Override
    public String toString() {
        return String.valueOf(textX) + String.valueOf(y);
    }
}
