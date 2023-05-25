package com.battleship.client;

/**
 * Custom exception class for Battleship game exceptions.
 */
public class BattleshipException extends Exception {

    /**
     * Constructs a BattleshipException with the specified error message.
     *
     * @param message the error message
     */
    public BattleshipException(String message) {
        super(message);
    }
}