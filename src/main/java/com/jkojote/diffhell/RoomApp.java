package com.jkojote.diffhell;

import com.jkojote.diffhell.app.Room;

import java.io.IOException;

public class RoomApp {
    public static void main(String[] args) throws IOException {
        new Room(DiffieHellman.generateKey(), 8080).start();
    }
}
