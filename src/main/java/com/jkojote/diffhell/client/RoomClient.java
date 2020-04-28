package com.jkojote.diffhell.client;


import com.jkojote.diffhell.Connection;
import com.jkojote.diffhell.security.Key;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.jkojote.diffhell.util.MathUtil.modExp;
import static com.jkojote.diffhell.util.MathUtil.nextInt;
import static com.jkojote.diffhell.util.StreamUtil.readInt;
import static com.jkojote.diffhell.util.StreamUtil.readLong;
import static com.jkojote.diffhell.util.StreamUtil.writeLong;
import static java.lang.Math.abs;

public class RoomClient {
    private final String host;
    private final int port;

    public RoomClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Connection connect() throws IOException {
        var socket = new Socket(host, port);
        log("Connected to the server");

        var in = socket.getInputStream();
        var out = socket.getOutputStream();

        var publicKey = readPublicKey(in);
        var roomPublicNumber = readRoomPublicNumber(in);
        var privateNumber = abs(nextInt(0xFFFF));
        var publicNumber = modExp(publicKey.getG(), privateNumber, publicKey.getP());
        sendPublicNumber(out, publicNumber);
        var desKey = modExp(roomPublicNumber, privateNumber, publicKey.getP());

        waitForApproval(in);
        return new Connection(socket, in, out, desKey);
    }

    private Key readPublicKey(InputStream in) throws IOException {
        log("Reading public key");

        var g = readInt(in);
        var p = readInt(in);
        return new Key(p, g);
    }

    private long readRoomPublicNumber(InputStream in) throws IOException {
        log("Reading public number");

        return readLong(in);
    }

    private void sendPublicNumber(OutputStream out, long publicNumber) throws IOException {
        log("Sending public number");

        writeLong(out, publicNumber);
    }

    private void waitForApproval(InputStream in) throws IOException {
        log("Waiting for approval...");

        if (readInt(in) == 1) {
            log("Approved. Connection established");
        }
    }

    private static void log(String message) {
        System.out.println("[CLIENT]: " + message);
    }
}
