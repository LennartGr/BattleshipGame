package com.battleship.client;

import org.javatuples.Triplet;

public class CoordinateParser {

    private static final String ERR_INVALID_INPUT = """
            Invalid input. Example of valid inputs: \"b4\" or \"b4h\"
             """;

    private static final String ERR_INVALID_INPUT_SIMPLE_COORDINATES = """
            Invalid input. Remove trailing \"h\" from the coordinates.
            """;
    private static final char HORIZONTAL_SPECIFIER = 'h';

    /**
     * 
     * Splits a string composed of a char and a number, for example "a14" into the
     * char and the number.
     * 
     * Throws an error if this is not possible.
     * 
     * If the string ends with the letter 'h', the method returns a triplet of the
     * form (char, number, true).
     * 
     * If not, the method returns a triplet of the form (char, number, false).
     * 
     * @param s the string to split
     * 
     * @return a triplet of the form (char, number, hasH) representing the split
     *         string
     * 
     * @throws IllegalArgumentException if the string cannot be split into a char
     *                                  and a number
     */
    public static Triplet<Character, Integer, Boolean> splitString(String str) throws BattleshipException {
        BattleshipException exception = new BattleshipException(ERR_INVALID_INPUT);
        if (str == null || str.length() < 2) {
            throw exception;
        }
        char character = str.charAt(0);
        if (!Character.isLetter(character)) {
            throw exception;
        }

        int number = 0;
        int index = 1;
        while (index < str.length() && Character.isDigit(str.charAt(index))) {
            number = number * 10 + Character.getNumericValue(str.charAt(index));
            index++;
        }
        boolean horizontal = false;
        if (index < str.length() && str.charAt(index) == HORIZONTAL_SPECIFIER) {
            horizontal = true;
            index++;
        }
        // check if string too long
        if (index != str.length()) {
            throw exception;
        }
        return new Triplet<>(character, number, horizontal);
    }

    public static Coordinates parseCoordinates(String str) throws BattleshipException {
        Triplet<Character, Integer, Boolean> triplet = CoordinateParser.splitString(str);
        if (triplet.getValue2()) {
            throw new BattleshipException(ERR_INVALID_INPUT_SIMPLE_COORDINATES);
        }
        return new Coordinates(triplet.getValue0(), triplet.getValue1());
    }

    
}
