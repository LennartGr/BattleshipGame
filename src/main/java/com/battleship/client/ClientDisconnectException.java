package com.battleship.client;

/**
 * Custom exception class representing a client disconnect event.
 */
public class ClientDisconnectException extends Exception {

    private final int clientId;

    /**
     * Constructs a new instance of the exception with the specified client ID.
     * 
     * @param clientId the ID of the disconnected client
     */
    public ClientDisconnectException(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Retrieves the ID of the disconnected client.
     * 
     * @return the ID of the disconnected client
     */
    public int getDisconnectId() {
        return this.clientId;
    }
}