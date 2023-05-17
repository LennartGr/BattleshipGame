package com.battleship.server;

import java.io.*;
import java.net.*;

import com.battleship.client.BattleshipException;
import com.battleship.client.Coordinates;
import com.battleship.client.ShipStorage;
import com.battleship.client.HitStatus;
import com.battleship.events.AttackerFeedbackEvent;
import com.battleship.events.DefenderFeedbackEvent;
import com.battleship.events.RoundStartEvent;
import com.battleship.events.RoundStartEvent.GameStatus;

public class Server {
    private ServerSocket serverSocket;

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

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(8080);
    }

    private static class MatchHandler implements Runnable {
        private final Socket[] clientSockets;
        private ObjectOutputStream[] outStreams = new ObjectOutputStream[2];
        private ObjectInputStream[] inStreams = new ObjectInputStream[2];
        private ShipStorage[] shipStorages = new ShipStorage[2];

        public MatchHandler(Socket socketA, Socket socketB) throws IOException {
            clientSockets = new Socket[] { socketA, socketB };
            for (int i = 0; i < clientSockets.length; i++) {
                outStreams[i] = new ObjectOutputStream(clientSockets[i].getOutputStream());
                inStreams[i] = new ObjectInputStream(clientSockets[i].getInputStream());
            }
        }

        public void sendObject(Object obj, int id) throws IOException {
            outStreams[id].writeObject(obj);
            outStreams[id].flush();
        }

        public Object receiveObject(int id) throws IOException, ClassNotFoundException {
            return inStreams[id].readObject();
        }

        @Override
        public void run() {
            try {
                // phase one: wait for ship storages of both players

                for (int i = 0; i < clientSockets.length; i++) {
                    Object obj = receiveObject(i);
                    shipStorages[i] = (ShipStorage) obj;
                    System.out.println("got ship storage from player " + i);
                    System.out.println(shipStorages[i].toString() + "\n");
                }

                // phase two: game, players attack each other and receive feedback
                int attackingPlayer = 0;
                while (true) {
                    if (isGameOver()) {
                        break;
                    }
                    // inform players who is attacking
                    sendObject(new RoundStartEvent(GameStatus.GAME_ON, true), attackingPlayer);
                    sendObject(new RoundStartEvent(GameStatus.GAME_ON, false), otherPlayer(attackingPlayer));

                    attackingPlayer = handleAttack(attackingPlayer);
                }

                // cleanup
                for (int i = 0; i < clientSockets.length; i++) {
                    inStreams[i].close();
                    outStreams[i].close();
                    clientSockets[i].close();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * checks if game is over.
         * if yes: players are informed via special RoundStartEvent
         * 
         * @return true iff game is over
         */
        private boolean isGameOver() throws IOException {
            // invariant: only one player may win at once (never a tie)
            for (int i = 0; i < 2; i++) {
                if (shipStorages[i].isCompletelyDestroyed()) {
                    int looser = i;
                    int winner = otherPlayer(i);
                    sendObject(new RoundStartEvent(GameStatus.YOU_WON, false), winner);
                    sendObject(new RoundStartEvent(GameStatus.YOU_LOST, false), looser);
                    return true;
                }
            }
            return false;
        }

        /*
         * handles attack of player with id attackingPlayer
         * 
         * @param attackingPlayer id of the player that attacks
         * @returns id of the player that attacks afterwards
         */
        private int handleAttack(int attackingPlayer) throws IOException, ClassNotFoundException {
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
            if (hitStatus == HitStatus.HIT || hitStatus == HitStatus.DESTROYED) {
                return attackingPlayer;
            } else {
                return defendingPlayer;
            }
        }

        /*
         * Returns id of the other player
         * 
         * @param player id of the player
         * @return id of the other player
         */
        private int otherPlayer(int player) {
            return (player + 1) % 2;
        }

        
    }
}
