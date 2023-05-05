package com.battleship.client;

import java.net.*;
import java.io.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String hostName, int port) throws IOException {
        socket = new Socket(hostName, port);
        System.out.println("Connected to server at " + socket.getRemoteSocketAddress());

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void run() throws IOException {
        connect("localhost", 8080);
        ShipStorageBuilder storageBuilder = new ShipStorageBuilder();
        ShipStorage shipStorage = storageBuilder.buildShipStorage(10, 10);
        sendObject(shipStorage);

        close();
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run();
    }
    
}

