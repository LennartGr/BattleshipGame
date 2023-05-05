package com.battleship.server;

import java.io.*;
import java.net.*;

import com.battleship.client.ShipStorage;

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

        public MatchHandler(Socket socketA, Socket socketB) throws IOException {
            clientSockets = new Socket[] {socketA, socketB};
            for (int i = 0; i < clientSockets.length; i++) {
                outStreams[i] = new ObjectOutputStream(clientSockets[i].getOutputStream());
                inStreams[i] = new ObjectInputStream(clientSockets[i].getInputStream());
            }
        }

        @Override
        public void run() {
            try {
                // phase one: wait for ship storages of both players
                ShipStorage[] shipStorages = new ShipStorage[2];
                for (int i = 0; i < clientSockets.length; i++) {
                    Object obj = inStreams[i].readObject();
                    shipStorages[i] = (ShipStorage) obj;
                    System.out.println("got ship storage from player " + i);
                    System.out.println(shipStorages[i].toString() + "\n");
                }
                // phase two: inform players that game started and tell them who attacks first

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
    }

    // deprecated
    private static class ConnectionHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectOutputStream out;
        private final ObjectInputStream in;

        public ConnectionHandler(Socket socket) throws IOException {
            clientSocket = socket;
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        }

        @Override
        public void run() {
            try {
                Object obj = in.readObject();
                // check what type of event it is and fetch parameters
                ShipStorage clientStorage = (ShipStorage) obj;
                System.out.println(clientStorage.toString());

                // Clean up resources
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
