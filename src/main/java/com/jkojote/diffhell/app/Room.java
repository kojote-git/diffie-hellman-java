package com.jkojote.diffhell.app;

import com.jkojote.diffhell.Key;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import static com.jkojote.diffhell.MathUtil.modExp;
import static com.jkojote.diffhell.MathUtil.nextInt;
import static com.jkojote.diffhell.StreamUtil.readLong;
import static com.jkojote.diffhell.StreamUtil.writeInt;
import static java.lang.Math.abs;


public class Room implements Closeable {
    private Key key;
    private List<Connection> connections;
    private ServerSocket serverSocket;

    public Room(Key key, int port) throws IOException {
        this.key = key;
        this.connections = new ArrayList<>();
        this.serverSocket = new ServerSocket(port);
    }

    public void start() {
        log("Starting the room");

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();

                log("Received a new connection");

                acceptConnection(socket);
            } catch (IOException ignore) {
                log("Connection lost");
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignore2) {
                    }
                }
            }
        }
    }

    private void acceptConnection(Socket socket) throws IOException {
        var in = socket.getInputStream();
        var out = socket.getOutputStream();

        var privateNumber = abs(nextInt(0xFFFF));
        var publicNumber = modExp(key.getG(), privateNumber, key.getP());
        sendPublicKey(out);
        sendPublicNumber(out, publicNumber);
        var clientPublicKey = readClientPublicNumber(in);
        var desKey = modExp(clientPublicKey, privateNumber, key.getP());
        approveConnection(out);

        var connection = new Connection(socket, in, out, desKey);
        addConnection(connection);
        connection.onMessageReceived(message -> broadcastMessage(connection, message));
    }

    private void sendPublicKey(OutputStream out) throws IOException {
        log("Sending public key...");

        writeInt(out, key.getG());
        writeInt(out, key.getP());
    }

    private void sendPublicNumber(OutputStream out, long publicNumber) throws IOException {
        log("Sending public number...");

        writeInt(out, (int) (publicNumber));
        writeInt(out, (int) (publicNumber >>> 32));
    }

    private long readClientPublicNumber(InputStream in) throws IOException {
        log("Reading client public number...");

        return readLong(in);
    }

    private void approveConnection(OutputStream out) throws IOException {
        log("Approving connection...");

        writeInt(out, 1);
    }

    private void broadcastMessage(Connection sender, String message) {
        log("Broadcasting message");

        for (var connection : new ArrayList<>(connections)) {
            if (!connection.equals(sender)) {
                sendMessageTo(connection, message);
            }
        }
    }

    private synchronized void addConnection(Connection connection) {
        log("Adding connection");

        this.connections.add(connection);
    }

    private void sendMessageTo(Connection connection, String message) {
        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            removeConnection(connection);

            try {
                connection.close();
            } catch (IOException ignore) {
            }
        }
    }

    private synchronized void removeConnection(Connection connection) {
        log("Removing connection");

        this.connections.remove(connection);
    }

    private void log(String message) {
        System.out.println("[ROOM]: " + message);
    }

    @Override
    public void close() throws IOException {
        for (var connection : connections) {
            try {
                connection.close();
            } catch (IOException ignored) {
            }
        }
    }
}
