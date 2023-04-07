package com.battleship.client;

public class ShipStorage {

    private static final String ERR_OVERLAP = "Cannot place ship there, it is overlapping with an existant one";
    private static final String ERR_BOARD_END = "Cannot place ship there, board is too small";
    
    private int height;
    private int width;
    private int[][] shipsArray;

    public ShipStorage(int width, int height) {
        this.height = height;
        this.width = width;
        this.shipsArray = new int[width][height];
    }

    public void addShip(Coordinates startCoordinates, boolean vertical, Ship ship) throws ShipPlacementException {
        int x = startCoordinates.getX();
        int y = startCoordinates.getY();
        int shipRemainingLength = ship.getLength();
        int[][] shipsCopy = copyArray(shipsArray);
        
        while (shipRemainingLength > 0) {
            if (x >= width || y >= height || x < 0 || y < 0) {
                throw new ShipPlacementException(ERR_BOARD_END);
            }
            if (shipsCopy[x][y] != 0) {
                throw new ShipPlacementException(ERR_OVERLAP);
            }
            shipsCopy[x][y] = ship.getId();
            if (vertical) {
                y++;
            } else {
                x++;
            }
            shipRemainingLength--;
        }
        this.shipsArray = shipsCopy;
    }

    public int getValue(Coordinates coordinates) {
        return shipsArray[coordinates.getX()][coordinates.getY()];
    }

    private static int[][] copyArray(int[][] arr) {
        int rows = arr.length;
        int cols = arr[0].length;
        int[][] copy = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = arr[i][j];
            }
        }
        return copy;
    }
    
}
