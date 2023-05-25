package com.battleship.client;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/**
 * The ShipStorage class represents the storage for ships in a battleship game.
 * It provides methods for adding ships, retrieving ship information, attacking
 * cells, and checking game completion status.
 */
public class ShipStorage implements Serializable {

    private static final String ERR_OVERLAP = "Cannot place ship there, it is overlapping with an existant one";
    private static final String ERR_BOARD_END = "Cannot place ship there, board is too small";
    private static final String ERR_ATTACK_END = "Cannot attack here, board is too small";
    private static final String ERR_ALREADY_ATTACKED = "You already attacked this spot!";

    private int height;
    private int width;
    private StorageEntry[][] shipsArray;

    // maps a ship id to the array of storage entries where the ship is placed on
    Map<Integer, StorageEntry[]> shipToEntriesMap = new HashMap<>();

    /**
     * Constructs a ShipStorage object with the specified width and height.
     *
     * @param width  The width of the storage.
     * @param height The height of the storage.
     */
    public ShipStorage(int width, int height) {
        this.height = height;
        this.width = width;
        this.shipsArray = new StorageEntry[width][height];
        // init ships array
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                shipsArray[x][y] = new StorageEntry();
            }
        }
    }

    /**
     * Adds a ship to the storage starting from the specified coordinates.
     *
     * @param startCoordinates The starting coordinates of the ship.
     * @param vertical         Flag indicating whether the ship is placed vertically
     *                         or horizontally.
     * @param ship             The ship to be added.
     * @throws BattleshipException if the ship placement is invalid.
     */
    public void addShip(Coordinates startCoordinates, boolean vertical, Ship ship) throws BattleshipException {
        int x = startCoordinates.getX();
        int y = startCoordinates.getY();
        int shipRemainingLength = ship.getLength();
        // first iteration: check if placement possible
        while (shipRemainingLength > 0) {
            if (x >= width || y >= height || x < 0 || y < 0) {
                throw new BattleshipException(ERR_BOARD_END);
            }
            if (shipsArray[x][y].shipId != null) {
                throw new BattleshipException(ERR_OVERLAP);
            }
            if (vertical) {
                y++;
            } else {
                x++;
            }
            shipRemainingLength--;
        }
        // second iteration: place ship
        // update shipToEntriesMap as well
        StorageEntry[] usedEntries = new StorageEntry[ship.getLength()];
        int currentEntryIndex = 0;

        x = startCoordinates.getX();
        y = startCoordinates.getY();
        shipRemainingLength = ship.getLength();
        while (shipRemainingLength > 0) {
            StorageEntry currentEntry = shipsArray[x][y];
            currentEntry.shipId = ship.getId();
            currentEntry.symbol = ship.getSymbol();
            usedEntries[currentEntryIndex++] = currentEntry;
            // prepare to look at next entry
            if (vertical) {
                y++;
            } else {
                x++;
            }
            shipRemainingLength--;
        }
        shipToEntriesMap.put(Integer.valueOf(ship.getId()), usedEntries);
    }

    /**
     * Retrieves the ship ID at the specified coordinates.
     *
     * @param coordinates The coordinates to check.
     * @return The ship ID at the specified coordinates, or null if no ship is
     *         present.
     */
    public Integer getValue(Coordinates coordinates) {
        return shipsArray[coordinates.getX()][coordinates.getY()].shipId;
    }

    /**
     * Retrieves the hit status at the specified coordinates.
     *
     * @param coordinates The coordinates to check.
     * @return The hit status at the specified coordinates.
     */
    public HitStatus getHitStatus(Coordinates coordinates) {
        return shipsArray[coordinates.getX()][coordinates.getY()].hitStatus;
    }

    /**
     * Attacks the cell at the specified coordinates and returns the hit status.
     * Assuming board is completely build up.
     *
     * @param coordinates The coordinates to attack.
     * @return The hit status after the attack.
     * @throws BattleshipException if the attack is invalid.
     */
    public HitStatus attack(Coordinates coordinates) throws BattleshipException {
        int x = coordinates.getX();
        int y = coordinates.getY();
        if (x >= width || y >= height || x < 0 || y < 0) {
            throw new BattleshipException(ERR_ATTACK_END);
        }
        StorageEntry entry = shipsArray[x][y];
        if (entry.hitStatus != HitStatus.NOT_ATTTACKED) {
            throw new BattleshipException(ERR_ALREADY_ATTACKED);
        }
        if (entry.shipId == null) {
            entry.hitStatus = HitStatus.MISSED;
            return HitStatus.MISSED;
        } else {
            entry.hitStatus = HitStatus.HIT;
            for (StorageEntry shipEntry : shipToEntriesMap.get(entry.shipId)) {
                // check if the ship was destroyed
                if (shipEntry.hitStatus != HitStatus.HIT) {
                    return HitStatus.HIT;
                }
            }
            // the ship was destroyed
            for (StorageEntry shipEntry : shipToEntriesMap.get(entry.shipId)) {
                shipEntry.hitStatus = HitStatus.DESTROYED;
            }
            return HitStatus.DESTROYED;
        }
    }

    /**
     * Checks if all ships in the storage are completely destroyed.
     *
     * @return true if all ships are destroyed, false otherwise.
     */
    public boolean isCompletelyDestroyed() {
        for (Integer ShipId : shipToEntriesMap.keySet()) {
            if (shipToEntriesMap.get(ShipId)[0].hitStatus != HitStatus.DESTROYED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the ship storage.
     *
     * @return The string representation of the ship storage.
     */
    @Override
    public String toString() {
        return storageEntryArrayVisualizer(this.shipsArray);
    }

    /**
     * The AttackHistory class represents the attack history of the ship storage.
     * It keeps track of the hit status of each cell in the storage.
     */
    class AttackHistory {

        private StorageEntry[][] attackHistory;

        /**
         * Constructs an AttackHistory object.
         */
        public AttackHistory() {
            this.attackHistory = new StorageEntry[width][height];
            // init ships array
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    attackHistory[x][y] = new StorageEntry();
                }
            }
        }

        /**
         * Sets the hit status at the specified coordinates in the attack history.
         *
         * @param coordinates The coordinates to set the hit status.
         * @param hitStatus   The hit status to set.
         */
        public void setHitStatus(Coordinates coordinates, HitStatus hitStatus) {
            attackHistory[coordinates.getX()][coordinates.getY()].hitStatus = hitStatus;
        }

        /**
         * Sets the hit status at the specified coordinates in the attack history.
         *
         * @param coordinates The coordinates to set the hit status.
         * @param hitStatus   The hit status to set.
         */
        @Override
        public String toString() {
            return storageEntryArrayVisualizer(attackHistory);
        }
    }

    /**
     * The StorageEntry class represents a storage entry in the ship storage.
     * It contains information about the presence of a ship, its symbol, and the hit
     * status.
     */
    class StorageEntry implements Serializable {

        // null if no ship is present
        private Integer shipId;
        private String symbol = " ";
        private HitStatus hitStatus = HitStatus.NOT_ATTTACKED;
    }

    /*
     * Convert a 2d array of storage entries to a String that can be displayed
     * with the jansi library
     */
    private static String storageEntryArrayVisualizer(StorageEntry[][] storageEntries) {
        final int width = storageEntries.length;
        final int height = storageEntries[0].length;
        String text = " |";
        String secondLine = "--";
        for (int i = 0; i < width; i++) {
            char current = (char) (65 + i);
            text += String.valueOf(current) + " ";
            secondLine += "--";
        }
        text += "\n" + secondLine + "\n";
        // print row by row
        for (int y = 0; y < height; y++) {
            String newline = String.valueOf(y) + "|";
            for (int x = 0; x < width; x++) {
                String backgroundColor = HitStatusColorizer.getColorString(storageEntries[x][y].hitStatus);
                String symbol = JansiHelper.colorizeBackground(storageEntries[x][y].symbol, backgroundColor);
                newline += symbol + " ";
            }
            text += newline + "\n";
        }
        // add explication in green
        text += HitStatusColorizer.EXPLICATION;
        return text;
    }
}
