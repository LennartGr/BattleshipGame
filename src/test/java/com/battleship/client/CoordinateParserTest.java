package com.battleship.client;

import org.junit.Test;
import static org.junit.Assert.*;

import org.javatuples.Triplet;

// Unit tests written by ChatGPT
public class CoordinateParserTest {

    @Test
    public void testSplitStringValid() throws BattleshipException {
        // Test valid input

        assertEquals(new Triplet<>('a', 14, false), CoordinateParser.splitString("a14"));
        assertEquals(new Triplet<>('z', 100, true), CoordinateParser.splitString("z100h"));
        assertEquals(new Triplet<>('G', 0, false), CoordinateParser.splitString("G0"));
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidChar() throws BattleshipException {
        // Test invalid input - non-letter character
        CoordinateParser.splitString("7h");
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidNoNumber() throws BattleshipException {
        // Test invalid input - no number
        CoordinateParser.splitString("a");
    }
    
    @Test(expected = BattleshipException.class)
    public void testSplitStringInvalidNoChar() throws BattleshipException {
        // Test invalid input - no character
        CoordinateParser.splitString("14");
    }

    @Test(expected = BattleshipException.class)
    public void testOtherInvalidInputs() throws BattleshipException {
        // Test invalid input - no character
        CoordinateParser.splitString("hh");
        CoordinateParser.splitString("hh2");
        CoordinateParser.splitString("hh2h");
    }
    
}
