package com.battleship.client;

public class ClientDisconnectException extends Exception {
    
    private final int clientId;

    public ClientDisconnectException(int clientId) {
        this.clientId = clientId;
    }

    public int getDisconnectId() {
        return this.clientId;
    }
}