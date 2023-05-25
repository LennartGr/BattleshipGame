package com.battleship.client;

import java.util.Scanner;
import org.javatuples.Triplet;

/**
 * The ShipStorageBuilder class is responsible for building a ShipStorage object by interactively
 * receiving user input for ship placement.
 */
public class ShipStorageBuilder {

    private static final String SHOW_BOARD = "show";
    private static final String PLACEMENT_INFO = """
            Place your ships. For each ship, specify its top left corner, for example \"b4\".\nAdd \"h\" to place the ship horizontally.""";
    private static final String ASK_TO_PLACE = "Place ship of length %d next. Type \"" + JansiHelper.alert(SHOW_BOARD)
            + "\" to see previous placements. %d ships left to place.";

    private Ship[] shipArray;

    /**
     * Constructs a ShipStorageBuilder object and prepares the ship configuration for a normal game.
     */
    public ShipStorageBuilder() {
        prepareNormalGame();
    }

    /**
     * Builds a ShipStorage object by interactively receiving user input for ship placement.
     *
     * @param scanner the Scanner object for receiving user input
     * @param width   the width of the ship storage
     * @param height  the height of the ship storage
     * @return the constructed ShipStorage object
     */
    public ShipStorage buildShipStorage(Scanner scanner, int width, int height) {
        ShipStorage shipStorage = new ShipStorage(width, height);
        JansiHelper.print(PLACEMENT_INFO);
        int counter = shipArray.length;
        for (Ship ship : shipArray) {
            JansiHelper.print(String.format(ASK_TO_PLACE, ship.getLength(), counter--));
            // loop until a valid position is chosen for the current ship
            while (true) {
                String input = scanner.nextLine();
                if (input.equals(SHOW_BOARD)) {
                    JansiHelper.print(shipStorage.toString());
                } else {
                    try {
                        Triplet<Character, Integer, Boolean> triplet = CoordinateParser.splitString(input);
                        Coordinates startCoordinates = new Coordinates(triplet.getValue0(), triplet.getValue1());
                        boolean horizontal = triplet.getValue2();
                        shipStorage.addShip(startCoordinates, !horizontal, ship);
                        // if success, move to the next ship
                        break;
                    } catch (BattleshipException e) {
                        JansiHelper.print(e.getMessage());
                    }
                }
            }
        }
        JansiHelper.print("Done placing ships. Waiting for the opponent to finish their placement...");
        return shipStorage;
    }

    // Prepare ship configuration for a normal game
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

    // Prepare ship configuration for a small game
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
