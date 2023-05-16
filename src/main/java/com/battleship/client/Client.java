package com.battleship.client;

import java.net.*;
import java.util.Scanner;

import com.battleship.events.AttackerFeedbackEvent;
import com.battleship.events.DefenderFeedbackEvent;
import com.battleship.events.StartMessageEvent;

import java.io.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // reused and closed at the very end
    private Scanner scanner = new Scanner(System.in);

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
        scanner.close();
    }

    public void run() throws IOException, ClassNotFoundException {
        // first phase: send shipStorage to server
        connect("localhost", 8080);
        ShipStorageBuilder storageBuilder = new ShipStorageBuilder();
        ShipStorage shipStorage = storageBuilder.buildShipStorage(scanner, 10, 10);
        sendObject(shipStorage);
        // second phase: wait for start message
        StartMessageEvent startMessage = (StartMessageEvent) receiveObject();
        System.out.println(startMessage.toString());

        boolean attacking = startMessage.getStarting();
        while (true) {
            if (attacking) {
                performAttack();
            } else {
                DefenderFeedbackEvent feedbackEvent = (DefenderFeedbackEvent) receiveObject();
                System.out.println("Cell " + feedbackEvent.coordinates().toString() + " was attacked. Result: "
                        + feedbackEvent.hitStatus().toString());
            }
            attacking = !attacking;
        }

        // TODO put back
        // close();
    }

    // fetches scanner input until it can be parsed to syntactically correct
    // coordinates
    public Coordinates parseCoordinatesToScanner() {
        Coordinates coordinates;
        while (true) {
            System.out.println("Enter coordinates where you want to attack:");
            String input = scanner.next();
            try {
                coordinates = CoordinateParser.parseCoordinates(input);
                break;
            } catch (BattleshipException e) {
                System.out.println(e.getMessage());
            }
        }
        return coordinates;
    }

    public void performAttack() throws IOException, ClassNotFoundException {
        // syntax check of input on client side:
        while (true) {
            Coordinates coordinates = parseCoordinatesToScanner();
            sendObject(coordinates);
            AttackerFeedbackEvent feedbackEvent = (AttackerFeedbackEvent) receiveObject();
            if (feedbackEvent.attackSuccess()) {
                System.out.println("Result of your attack: " + feedbackEvent.hitStatus().toString());
                return;
            } else {
                System.out.println(feedbackEvent.exception().toString());
                System.out.println("You may try to attack again!");
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Client client = new Client();
        client.run();
    }

}
