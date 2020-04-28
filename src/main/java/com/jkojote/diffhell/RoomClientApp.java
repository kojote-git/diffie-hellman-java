package com.jkojote.diffhell;

import com.jkojote.diffhell.app.RoomClient;

import java.io.IOException;

public class RoomClientApp {
    public static void main(String[] args) throws IOException {
        var client = new RoomClient("localhost", 8080);
        var connection = client.connect();

        connection.sendMessage("Hello world");
        connection.close();
    }
}
