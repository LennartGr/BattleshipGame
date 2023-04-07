package com.battleship;

import com.battleship.client.ShipStorageBuilder;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ShipStorageBuilder builder = new ShipStorageBuilder();
        builder.buildShipStorage(10, 10);
    }
}
