package com.battleship.client;

import java.net.*;
import java.util.Scanner;

import com.battleship.events.AttackerFeedbackEvent;
import com.battleship.events.DefenderFeedbackEvent;
import com.battleship.events.RoundStartEvent;
import com.battleship.events.RoundStartEvent.GameStatus;

// use colorful console output
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;


import java.io.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ShipStorage shipStorage;

    private static final String COMMAND_SHOW_OWN = "showown";
    private static final String INPUT_DEMAND_ATTACK = "Enter coordinates where you want to attack. Use \'"
            + COMMAND_SHOW_OWN + "\' to see the state of your ships.";

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
        shipStorage = storageBuilder.buildShipStorage(scanner, 10, 10);
        sendObject(shipStorage);

        // second phase: wait for start message
        // StartMessageEvent startMessage = (StartMessageEvent) receiveObject();
        // System.out.println(startMessage.toString());

        // boolean attacking = startMessage.getStarting();

        while (true) {
            final RoundStartEvent roundStartEvent = (RoundStartEvent) receiveObject();
            if (roundStartEvent.gameStatus() == GameStatus.YOU_LOST) {
                System.out.println("You lost!");
                break;
            } else if (roundStartEvent.gameStatus() == GameStatus.YOU_WON) {
                System.out.println("You won!");
                break;
            }
            // arriving here: game not over
            final boolean attacking = roundStartEvent.attacking();
            System.out.println(roundStartEvent.toString());

            if (attacking) {
                performAttack();
            } else {
                DefenderFeedbackEvent feedbackEvent = (DefenderFeedbackEvent) receiveObject();
                System.out.printf("Cell %s was attacked. Result: %s.%n",
                        feedbackEvent.coordinates().toString(),
                        feedbackEvent.hitStatus().toString());
                // update own ship storage accordingly
                try {
                    shipStorage.attack(feedbackEvent.coordinates());
                } catch (BattleshipException e) {
                    // invariant: exception never thrown since server ensured that attack was legal
                }
            }
        }

        close();
    }

    private void performAttack() throws IOException, ClassNotFoundException {
        // syntax check of input on client side:
        while (true) {
            Coordinates coordinates = parseCoordinatesToScanner();
            sendObject(coordinates);
            AttackerFeedbackEvent feedbackEvent = (AttackerFeedbackEvent) receiveObject();
            if (feedbackEvent.attackSuccess()) {
                System.out.println("Result of your attack: " + feedbackEvent.hitStatus().toString());
                return;
            } else {
                System.out.println(feedbackEvent.exception().getMessage());
                System.out.println("You may try to attack again!");
            }
        }
        // TODO update some history of own attacks to reflect this attack (2d array with
        // hit statuses)
    }

    // fetches scanner input until it can be parsed to syntactically correct
    // coordinates
    private Coordinates parseCoordinatesToScanner() {
        Coordinates coordinates;
        while (true) {
            // TODO give player the option to view some information as well (own board or
            // attack history)
            String input = catchSpecialInputs();
            try {
                coordinates = CoordinateParser.parseCoordinates(input);
                break;
            } catch (BattleshipException e) {
                System.out.println(e.getMessage());
            }
        }
        return coordinates;
    }

    // if input is a known command, execute it
    private String catchSpecialInputs() {
        while (true) {
            System.out.println(INPUT_DEMAND_ATTACK);
            String input = scanner.next();
            if (input.equals(COMMAND_SHOW_OWN)) {
                System.out.println(ansi().render(shipStorage.toString()));
            } else {
                return input;
            }
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // allow for colorful console output
        AnsiConsole.systemInstall();

        Client client = new Client();
        client.run();
    }

}
