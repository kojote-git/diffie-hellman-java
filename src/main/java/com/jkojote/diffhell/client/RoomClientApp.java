package com.jkojote.diffhell.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomClientApp {
    private static final List<Message> MESSAGES_RECEIVED = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        var client = new RoomClient("localhost", 8080);
        var in = new BufferedReader(new InputStreamReader(System.in));

        try (var connection = client.connect()) {
            connection.onMessageReceived(message -> {
                MESSAGES_RECEIVED.add(new Message(message, LocalDateTime.now()));
            });

            printUsage();
            printPrompt();
            while (true) {
                var cmd = in.readLine();

                if (cmd.startsWith("\\m ")) {
                    var message = cmd.replace("\\m ", "");
                    connection.sendMessage(message);
                } else if (cmd.startsWith("\\l")) {
                    listAllMessages();
                } else {
                    System.out.println("unknown command");
                    printUsage();
                }
                printPrompt();
            }
        }
    }

    private static void listAllMessages() {
        for (var message : MESSAGES_RECEIVED) {
            System.out.println(message);
            System.out.println();
        }
        MESSAGES_RECEIVED.clear();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("\\m message - send a message");
        System.out.println("\\l - list all received messages");
    }

    private static void printPrompt() {
        System.out.print("cmd: ");
    }
}
