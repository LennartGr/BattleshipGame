package com.battleship.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for ship storage
 */
public class ShipStorageTest {

    private ShipStorage storage;
    private Ship ship1;
    private Ship ship2;
    private int height = 10;
    private int width = 10;

    @Before
    public void setUp() {
        storage = new ShipStorage(width, height);
        ship1 = new Ship(1, 2, "x");
        ship2 = new Ship(2, 4, "x");
    }

    @Test
    public void invalidCoordinates() {
        assertThrows(ShipPlacementException.class, () -> {
            storage.addShip(new Coordinates('a', -1), false, ship1);
        });

        assertEquals(storage.getValue(new Coordinates('a', 0)), 0);

        assertThrows(ShipPlacementException.class, () -> {
            storage.addShip(new Coordinates('a', height), true, ship1);
        });
    }

    @Test 
    public void setAndGet() throws ShipPlacementException {
        Coordinates co = new Coordinates('a', 0);
        storage.addShip(co, false, ship1);
        assertEquals(storage.getValue(co), ship1.getId());
        co = new Coordinates('b', 0);
        assertEquals(storage.getValue(co), ship1.getId());
        co = new Coordinates('c', 0);
        assertEquals(storage.getValue(co), 0);

        // place ship where there's already one
        assertThrows(ShipPlacementException.class, () -> {
            Coordinates conew = new Coordinates('b', 0);
            storage.addShip(conew, true, ship2);
        });
    }
}
