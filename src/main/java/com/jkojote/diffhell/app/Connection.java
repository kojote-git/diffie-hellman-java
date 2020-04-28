package com.jkojote.diffhell.app;

import com.jkojote.diffhell.Des;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.function.Consumer;


public class Connection {
    private static final Des DES = new Des();

    private final Socket socket;
    private final Writer out;
    private final Reader in;
    private final long desKey;

    private boolean closed;
    private Thread incomingMessagesThread;
    private Consumer<String> onMessageReceived;

    public Connection(Socket socket, InputStream in, OutputStream out, long desKey) {
        this.socket = socket;
        this.out = new OutputStreamWriter(out);
        this.in = new InputStreamReader(in);
        this.desKey = desKey;
        startListeningForMessages();
    }

    private void startListeningForMessages() {
        incomingMessagesThread = new Thread(() -> {
            try {
                listenForIncomingMessages();
            } catch (IOException ignored) {
            } finally {
                try {
                    close();
                } catch (IOException ignored) {
                    // OK, do nothing, just finish the thread
                }
            }
        });
        incomingMessagesThread.start();
    }

    private void listenForIncomingMessages() throws IOException {
        var in = new BufferedReader(this.in);

        while (true) {
            var message = in.readLine();

            if (message == null) {
                return;
            }

            notifyMessageReceived(DES.decryptString(message, desKey));
        }
    }

    private void notifyMessageReceived(String message) {
        if (onMessageReceived != null) {
            onMessageReceived.accept(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        if (isClosed()) {
            throw new IOException("Connection is closed");
        }

        var encryptedMessage = DES.encryptString(message, desKey);
        out.write(encryptedMessage);
        out.write("\n");
    }

    public void onMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        synchronized (this) {
            if (isClosed()) {
                return;
            }
            if (incomingMessagesThread != null && incomingMessagesThread.isAlive()) {
                incomingMessagesThread.interrupt();
            }
            closed = true;
            socket.close();
        }
    }
}
