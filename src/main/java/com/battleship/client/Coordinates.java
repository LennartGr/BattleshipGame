package com.battleship.client;

public class Coordinates {

    private final char textX;
    private final int x;
    private final int y;

    public Coordinates(char textX, int numberY) {
        this.textX = textX;
        this.x = charToInt(textX);
        this.y = numberY;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
        this.textX = (char) (x + 65);
    }

    public int getX() {
        return this.x;
    }

    public char getXtext() {
        return this.textX;
    }

    public int getY() {
        return this.y;
    }

    public static int charToInt(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else {
            throw new IllegalArgumentException("Input character is not a letter.");
        }
    }
}
