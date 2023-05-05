package com.battleship.events;

import com.battleship.client.ShipStorage;

public class SendShipstorageEvent extends Event {
    
    private ShipStorage shipStorage;

    public SendShipstorageEvent(ShipStorage shipStorage) {
        super();
        this.shipStorage = shipStorage;
    }

    public ShipStorage getShipStorage() {
        return shipStorage;
    }
}
