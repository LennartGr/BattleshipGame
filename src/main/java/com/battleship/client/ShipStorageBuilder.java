package com.battleship.client;

import java.util.Scanner;
import org.javatuples.Triplet;

public class ShipStorageBuilder {

    private static final char HORIZONTAL_SPECIFIER = 'h';
    private static final String SHOW_BOARD = "show";
    private static final String PLACEMENT_INFO = """
            Place your ships. For each ship, specify its top left corner, for example \"b4\".\nAdd \"h\" to place the ship horizontally.""";
    private static final String ASK_TO_PLACE = "Place ship of length %d next. Type \"" + SHOW_BOARD
            + "\" to see previous placements.";
    private static final String ERR_INVALID_INPUT = """
            Invalid input. Example of valid inputs: \"b4\" or \"b4h\"
             """;

    private Ship[] shipArray;

    public ShipStorageBuilder() {
        prepareSmallGame();
    }

    public ShipStorage buildShipStorage(int width, int height) {
        ShipStorage shipStorage = new ShipStorage(width, height);
        Scanner scanner = new Scanner(System.in);
        System.out.println(PLACEMENT_INFO);
        for (Ship ship : shipArray) {
            System.out.println(String.format(ASK_TO_PLACE, ship.getLength()));
            // loop until a valid position is choosen for the current ship
            while (true) {
                String input = scanner.nextLine();
                if (input.equals(SHOW_BOARD)) {
                    System.out.println(shipStorage.toString());
                } else {
                    try {
                        Triplet<Character, Integer, Boolean> triplet = splitString(input);
                        Coordinates startCoordinates = new Coordinates(triplet.getValue0(), triplet.getValue1());
                        boolean horizontal = triplet.getValue2();
                        shipStorage.addShip(startCoordinates, !horizontal, ship);
                        // if success, next ship
                        break;
                    } catch (BattleshipException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        scanner.close();
        return shipStorage;
    }

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

    private void prepareNormalGame() {
        int id = 1;
        shipArray = new Ship[] {
                new Ship(id++, 5, "x"),
                new Ship(id++, 4, "x"),
                new Ship(id++, 4, "x"),
                new Ship(id++, 3, "x"),
                new Ship(id++, 3, "x"),
                new Ship(id++, 3, "x"),
                new Ship(id++, 2, "x"),
                new Ship(id++, 2, "x"),
                new Ship(id++, 2, "x"),
                new Ship(id++, 2, "x"),
        };
    }

    private void prepareSmallGame() {
        int id = 1;
        shipArray = new Ship[] {
                new Ship(id++, 5, "x"),
                // new Ship(id++, 4, "x"),
                // new Ship(id++, 3, "x"),
                // new Ship(id++, 2, "x"),
        };
    }
}
