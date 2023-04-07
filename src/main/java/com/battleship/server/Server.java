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
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getRemoteSocketAddress());

            // Start a new thread to handle the client connection
            Thread t = new Thread(new ConnectionHandler(clientSocket));
            t.start();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(8080);
    }

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
