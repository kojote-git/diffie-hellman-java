package com.jkojote.diffhell.app;


import com.jkojote.diffhell.Key;
import com.jkojote.diffhell.MathUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.jkojote.diffhell.MathUtil.modExp;
import static com.jkojote.diffhell.MathUtil.nextInt;
import static com.jkojote.diffhell.StreamUtil.readInt;
import static com.jkojote.diffhell.StreamUtil.readLong;
import static com.jkojote.diffhell.StreamUtil.writeLong;

public class RoomClient {
    private final String host;
    private final int port;

    public RoomClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Connection connect() throws IOException {
        var socket = new Socket(host, port);
        var in = socket.getInputStream();
        var out = socket.getOutputStream();

        var publicKey = readPublicKey(in);
        var roomPublicNumber = readRoomPublicNumber(in);
        var privateNumber = nextInt();
        var publicNumber = modExp(publicKey.getG(), privateNumber, publicKey.getP());
        sendPublicNumber(out, publicNumber);
        var desKey = modExp(roomPublicNumber, privateNumber, publicKey.getP());

        return new Connection(socket, in, out, desKey);
    }

    private Key readPublicKey(InputStream in) throws IOException {
        var g = readInt(in);
        var p = readInt(in);
        return new Key(g, p);
    }

    private long readRoomPublicNumber(InputStream in) throws IOException {
        return readLong(in);
    }

    private void sendPublicNumber(OutputStream out, long publicNumber) throws IOException {
        writeLong(out, publicNumber);
    }

}
