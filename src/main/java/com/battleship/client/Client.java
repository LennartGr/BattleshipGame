package com.battleship.client;

import java.net.*;

import com.battleship.events.StartMessageEvent;

import java.io.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String hostName, int port) throws IOException {
        socket = new Socket(hostName, port);
        System.out.println("Connected to server at " + socket.getRemoteSocketAddress());

        out = new ObjectOutputStream(socket.getOutputStream());
        // TODO program seems to pause in this line if server thread not yet started
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

    public void run() throws IOException, ClassNotFoundException {
        // first phase: send shipStorage to server
        connect("localhost", 8080);
        ShipStorageBuilder storageBuilder = new ShipStorageBuilder();
        ShipStorage shipStorage = storageBuilder.buildShipStorage(10, 10);
        sendObject(shipStorage);
        // second phase: wait for start message
        StartMessageEvent startMessage = (StartMessageEvent) receiveObject();
        System.out.println(startMessage.toString());

        close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.run();
    }
    
}

