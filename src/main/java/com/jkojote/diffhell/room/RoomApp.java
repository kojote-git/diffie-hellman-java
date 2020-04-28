package com.jkojote.diffhell.room;

import com.jkojote.diffhell.security.DiffieHellman;

import java.io.IOException;

public class RoomApp {
    public static void main(String[] args) throws IOException {
        new Room(DiffieHellman.generateKey(), 8080).start();
    }
}
