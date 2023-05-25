package com.battleship.client;

import java.net.*;
import java.util.Scanner;

import com.battleship.events.AttackerFeedbackEvent;
import com.battleship.events.DefenderFeedbackEvent;
import com.battleship.events.RoundStartEvent;
import com.battleship.events.RoundStartEvent.AttackStatus;
import com.battleship.events.RoundStartEvent.GameStatus;

// use colorful console output
import org.fusesource.jansi.AnsiConsole;

import java.io.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ShipStorage shipStorage;
    private ShipStorage.AttackHistory attackHistory;

    private static final String COMMAND_SHOW_OWN = "showown";
    private static final String COMMAND_SHOW_HISTORY = "history";
    private static final String INPUT_DEMAND_ATTACK = String.format(
            "Enter coordinates where you want to attack. Use \'%s\' to see the state of your ships. Use \'%s\' to see your attack history",
            JansiHelper.alert(COMMAND_SHOW_OWN), JansiHelper.alert(COMMAND_SHOW_HISTORY));

    // reused and closed at the very end
    private Scanner scanner = new Scanner(System.in);

    /**
     * Establishes a connection with the server.
     *
     * @param hostName the hostname of the server
     * @param port     the port number of the server
     * @throws IOException if an I/O error occurs while establishing the connection
     */
    private void connect(String hostName, int port) throws IOException {
        socket = new Socket(hostName, port);
        JansiHelper.print("Connected to server at " + socket.getRemoteSocketAddress());

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Sends an object to the server.
     *
     * @param obj the object to send
     * @throws IOException if an I/O error occurs while sending the object
     */
    private void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }

    /**
     * Receives an object from the server.
     *
     * @return the received object
     * @throws ClientDisconnectException if the opponent has disconnected from the game
     * @throws IOException               if an I/O error occurs while receiving the object
     * @throws ClassNotFoundException  if error in protocol occurs
     */
    private Object receiveObject() throws ClientDisconnectException, IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof ClientDisconnectException) {
            throw (ClientDisconnectException) obj;
        }
        return obj;
    }

    /**
     * Closes the client's connection and resources.
     *
     * @throws IOException if an I/O error occurs while closing the connection
     */
    private void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        scanner.close();
    }

    /**
     * Runs the client-side game logic.
     *
     * @throws ClassNotFoundException if error in protocol occurs
     */
    public void run() throws ClassNotFoundException {
        try {
            // Connect to the server
            connect("localhost", 8080);
            
            // Build the ship storage
            ShipStorageBuilder storageBuilder = new ShipStorageBuilder();
            shipStorage = storageBuilder.buildShipStorage(scanner, 10, 10);
            
            // Send ship storage to the server
            sendObject(shipStorage);

            // Create attack history
            attackHistory = shipStorage.new AttackHistory();

            while (true) {
                // Receive round start event from the server
                final RoundStartEvent roundStartEvent = (RoundStartEvent) receiveObject();
                if (roundStartEvent.gameStatus() == GameStatus.YOU_LOST) {
                    JansiHelper.print("You lost!");
                    break;
                } else if (roundStartEvent.gameStatus() == GameStatus.YOU_WON) {
                    JansiHelper.print("You won!");
                    break;
                }

                // Determine if it's the client's turn to attack
                final boolean attacking = (roundStartEvent.attackStatus() == AttackStatus.ATTACK
                        || roundStartEvent.attackStatus() == AttackStatus.ATTACK_AGAIN);

                JansiHelper.print(roundStartEvent.toString());

                if (attacking) {
                    // Perform the attack
                    performAttack();
                } else {
                    // Receive feedback from the defender
                    DefenderFeedbackEvent feedbackEvent = (DefenderFeedbackEvent) receiveObject();
                    JansiHelper.print(String.format("Cell %s was attacked. Result: %s.%n",
                            feedbackEvent.coordinates().toString(),
                            feedbackEvent.hitStatus().toString()));
                    // Update own ship storage accordingly
                    try {
                        shipStorage.attack(feedbackEvent.coordinates());
                    } catch (BattleshipException e) {
                        // This exception should never be thrown since the server ensures that the attack was legal
                    }
                }
            }
            close();
        } catch (ClientDisconnectException e) {
            JansiHelper.print("The other player disconnected, you won.");
        } catch (IOException e) {
            JansiHelper.print("Communication with server failed, game terminated.");
        }
    }

    /**
     * Performs an attack by sending the coordinates to the server and receiving feedback.
     * Synatex check of the inputed coordinates happens on the client side.
     *
     * @throws ClientDisconnectException if the opponent has disconnected from the game
     * @throws IOException               if an I/O error occurs while performing the attack
     * @throws ClassNotFoundException  if error in protocol occurs
     */
    private void performAttack() throws ClientDisconnectException, IOException, ClassNotFoundException {
        while (true) {
            // Parse the input coordinates from the scanner
            Coordinates coordinates = parseCoordinatesToScanner();
            sendObject(coordinates);
            AttackerFeedbackEvent feedbackEvent = (AttackerFeedbackEvent) receiveObject();
            if (feedbackEvent.attackSuccess()) {
                JansiHelper.print("Result of your attack: " + feedbackEvent.hitStatus().toString());
                // Update attack history
                attackHistory.setHitStatus(coordinates, feedbackEvent.hitStatus());
                return;
            } else {
                JansiHelper.print(feedbackEvent.exception().getMessage());
                JansiHelper.print("You may try to attack again!");
            }
        }
    }

    /**
     * Parses coordinates from the scanner until valid coordinates are entered.
     *
     * @return the parsed coordinates
     */
    private Coordinates parseCoordinatesToScanner() {
        Coordinates coordinates;
        while (true) {
            String input = catchSpecialInputs();
            try {
                coordinates = CoordinateParser.parseCoordinates(input);
                break;
            } catch (BattleshipException e) {
                JansiHelper.print(e.getMessage());
            }
        }
        return coordinates;
    }

    /**
     * Checks if the input is a special command and executes it if it is.
     *
     * @return the input string if it is not a special command, or the special command itself
     */
    private String catchSpecialInputs() {
        while (true) {
            JansiHelper.print(INPUT_DEMAND_ATTACK);
            String input = scanner.next();
            if (input.equals(COMMAND_SHOW_OWN)) {
                JansiHelper.print(shipStorage.toString());
            } else if (input.equals(COMMAND_SHOW_HISTORY)) {
                JansiHelper.print(attackHistory.toString());
            } else {
                return input;
            }
        }
    }

    /**
     * The main method to start the Battleship game client.
     *
     * @param args the command-line arguments
     * @throws IOException              if an I/O error occurs while running the game
     * @throws ClassNotFoundException if a class cannot be found while running the game (error in protocol)
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Allow for colorful console output
        AnsiConsole.systemInstall();

        // Create and run the client
        Client client = new Client();
        client.run();
    }

}
