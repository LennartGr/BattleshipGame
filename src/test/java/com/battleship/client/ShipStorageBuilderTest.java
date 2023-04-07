package com.battleship.client;

import org.junit.Test;
import static org.junit.Assert.*;

import org.javatuples.Triplet;

// Unit tests written by ChatGPT
public class ShipStorageBuilderTest {

    @Test
    public void testSplitStringValid() throws BattleshipException {
        // Test valid input

        assertEquals(new Triplet<>('a', 14, false), ShipStorageBuilder.splitString("a14"));
        assertEquals(new Triplet<>('z', 100, true), ShipStorageBuilder.splitString("z100h"));
        assertEquals(new Triplet<>('G', 0, false), ShipStorageBuilder.splitString("G0"));
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidChar() throws BattleshipException {
        // Test invalid input - non-letter character
        ShipStorageBuilder.splitString("7h");
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidNoNumber() throws BattleshipException {
        // Test invalid input - no number
        ShipStorageBuilder.splitString("a");
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidNoChar() throws BattleshipException {
        // Test invalid input - no character
        ShipStorageBuilder.splitString("14");
    }

    @Test(expected = BattleshipException.class)
    public void testOtherInvalidInputs() throws BattleshipException {
        // Test invalid input - no character
        ShipStorageBuilder.splitString("hh");
        ShipStorageBuilder.splitString("hh2");
        ShipStorageBuilder.splitString("hh2h");
    }
    
}
