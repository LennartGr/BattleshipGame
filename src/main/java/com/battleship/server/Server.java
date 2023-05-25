package com.battleship.server;

import java.io.*;
import java.net.*;

import com.battleship.client.BattleshipException;
import com.battleship.client.ClientDisconnectException;
import com.battleship.client.Coordinates;
import com.battleship.client.ShipStorage;
import com.battleship.client.HitStatus;
import com.battleship.events.AttackerFeedbackEvent;
import com.battleship.events.DefenderFeedbackEvent;
import com.battleship.events.RoundStartEvent;
import com.battleship.events.RoundStartEvent.AttackStatus;
import com.battleship.events.RoundStartEvent.GameStatus;

/**
 * Server class that handles the game logic for a Battleship game.
 */
public class Server {
    private ServerSocket serverSocket;

    /**
     * Starts the server on the specified port.
     *
     * @param port the port to start the server on
     * @throws IOException if an I/O error occurs when opening the server socket
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocketA = serverSocket.accept();
            System.out.println("Client A connected from " + clientSocketA.getRemoteSocketAddress());

            Socket clientSocketB = serverSocket.accept();
            System.out.println("Client B connected from " + clientSocketB.getRemoteSocketAddress());
            System.out.println("Game starts now!");

            Thread t = new Thread(new MatchHandler(clientSocketA, clientSocketB));

            // Start a new thread to handle the client connection
            // Thread t = new Thread(new ConnectionHandler(clientSocket));
            t.start();
        }
    }

    /**
     * The main method to start the server.
     *
     * @param args the command-line arguments
     * @throws IOException if an I/O error occurs when starting the server
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(8080);
    }

    /**
     * Runnable class that handles a match between two clients.
     */
    private static class MatchHandler implements Runnable {
        private final Socket[] clientSockets;
        private ObjectOutputStream[] outStreams = new ObjectOutputStream[2];
        private ObjectInputStream[] inStreams = new ObjectInputStream[2];
        private ShipStorage[] shipStorages = new ShipStorage[2];

        /**
         * Constructs a MatchHandler with the given client sockets.
         *
         * @param socketA the socket for client A
         * @param socketB the socket for client B
         * @throws IOException if an I/O error occurs when creating the object streams
         */
        public MatchHandler(Socket socketA, Socket socketB) throws IOException {
            clientSockets = new Socket[] { socketA, socketB };
            for (int i = 0; i < clientSockets.length; i++) {
                outStreams[i] = new ObjectOutputStream(clientSockets[i].getOutputStream());
                inStreams[i] = new ObjectInputStream(clientSockets[i].getInputStream());
            }
        }

        /**
         * Sends an object to the specified client.
         *
         * @param obj the object to send
         * @param id  the ID of the client
         * @throws ClientDisconnectException if the client has disconnected
         */
        public void sendObject(Object obj, int id) throws ClientDisconnectException {
            try {
                outStreams[id].writeObject(obj);
                outStreams[id].flush();
            } catch (IOException e) {
                throw new ClientDisconnectException(id);
            }
        }

        /**
         * Receives an object from the specified client.
         *
         * @param id the ID of the client
         * @return the received object
         * @throws ClientDisconnectException if the client has disconnected
         * @throws ClassNotFoundException    if error in protocol occured
         */
        public Object receiveObject(int id) throws ClientDisconnectException, ClassNotFoundException {
            try {
                return inStreams[id].readObject();
            } catch (IOException e) {
                throw new ClientDisconnectException(id);
            }
        }

        /**
         * Runs the match handling logic.
         */
        @Override
        public void run() {
            try {
                // phase one: wait for ship storages of both players

                for (int i = 0; i < clientSockets.length; i++) {
                    Object obj = receiveObject(i);
                    shipStorages[i] = (ShipStorage) obj;
                    System.out.println("received ship storage from player " + i);
                }

                // phase two: game, players attack each other and receive feedback
                int attackingPlayer = 0;
                // remembers whether the attacking player is attacking or even attacking again
                // because the last attack was successful
                AttackStatus currentAttackStatus = AttackStatus.ATTACK;
                AttackStatus currentDefendStatus = AttackStatus.DEFEND;
                while (true) {
                    if (isGameOver()) {
                        break;
                    }
                    // inform players who is attacking
                    sendObject(new RoundStartEvent(GameStatus.GAME_ON, currentAttackStatus), attackingPlayer);
                    sendObject(new RoundStartEvent(GameStatus.GAME_ON, currentDefendStatus),
                            otherPlayer(attackingPlayer));

                    boolean mayAttackAgain = handleAttack(attackingPlayer);
                    // update variables to inform players precisely in next round whether they are
                    // defending or attacking (again)
                    if (mayAttackAgain) {
                        currentAttackStatus = AttackStatus.ATTACK_AGAIN;
                        currentDefendStatus = AttackStatus.DEFEND_AGAIN;
                    } else {
                        attackingPlayer = otherPlayer(attackingPlayer);
                        currentAttackStatus = AttackStatus.ATTACK;
                        currentDefendStatus = AttackStatus.DEFEND;
                    }
                }

                // cleanup
                for (int i = 0; i < clientSockets.length; i++) {
                    inStreams[i].close();
                    outStreams[i].close();
                    clientSockets[i].close();
                }
            } catch (ClientDisconnectException e) {
                // inform other client that he won because of disconnect of the first client
                int disconnectId = e.getDisconnectId();
                System.out.println("Match ended unexpectedly because a player disconnected.");
                try {
                    sendObject(e, otherPlayer(disconnectId));
                } catch (ClientDisconnectException e2) {
                    // both clients disconnected, don't do anynight
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * Checks if the game is over.
         *
         * @return true if the game is over, false otherwise
         * @throws ClientDisconnectException if a client has disconnected
         */
        private boolean isGameOver() throws ClientDisconnectException {
            // invariant: only one player may win at once (never a tie)
            for (int i = 0; i < 2; i++) {
                if (shipStorages[i].isCompletelyDestroyed()) {
                    int looser = i;
                    int winner = otherPlayer(i);
                    sendObject(new RoundStartEvent(GameStatus.YOU_WON, null), winner);
                    sendObject(new RoundStartEvent(GameStatus.YOU_LOST, null), looser);
                    return true;
                }
            }
            return false;
        }

        /**
         * Handles the attack of the attacking player.
         *
         * @param attackingPlayer the ID of the attacking player
         * @return true if the attacking player may attack again, false otherwise
         * @throws ClientDisconnectException if a client has disconnected
         * @throws ClassNotFoundException    if the class of the received object cannot
         *                                   be found
         */
        private boolean handleAttack(int attackingPlayer) throws ClientDisconnectException, ClassNotFoundException {
            int defendingPlayer = otherPlayer(attackingPlayer);
            HitStatus hitStatus;
            Coordinates attackCoordinates;
            while (true) {
                attackCoordinates = (Coordinates) receiveObject(attackingPlayer);
                try {
                    hitStatus = shipStorages[defendingPlayer].attack(attackCoordinates);
                    // no exception thrown: attack was semantically correct
                    break;
                } catch (BattleshipException e) {
                    AttackerFeedbackEvent event = new AttackerFeedbackEvent(false, null, e);
                    sendObject(event, attackingPlayer);
                }
            }
            // inform both players about attack
            AttackerFeedbackEvent attackerEvent = new AttackerFeedbackEvent(true, hitStatus, null);
            sendObject(attackerEvent, attackingPlayer);
            DefenderFeedbackEvent defenderEvent = new DefenderFeedbackEvent(attackCoordinates, hitStatus);
            sendObject(defenderEvent, defendingPlayer);

            // use hitStatus to find out who attacks afterwards
            return (hitStatus == HitStatus.HIT || hitStatus == HitStatus.DESTROYED);
        }

        /**
         * Returns the ID of the other player.
         *
         * @param player the ID of the player
         * @return the ID of the other player
         */
        private int otherPlayer(int player) {
            return (player + 1) % 2;
        }

    }
}
