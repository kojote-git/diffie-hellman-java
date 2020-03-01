package com.jkojote.diffhell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.jkojote.diffhell.MathUtil.modExp;

public class Main {
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        while (true) {
            var key = DiffieHellman.generateKey();

            System.out.println("Key: " + key);

            System.out.print("Alice, enter a number: ");
            var a = readInt();

            System.out.print("Bob, enter a number: ");
            var b = readInt();

            System.out.println("Alices private number: " + a);
            System.out.println("Bobs private number: " + b);

            var x = modExp(key.getG(), a, key.getP());
            var y = modExp(key.getG(), b, key.getP());

            System.out.println("Alices public key: " + x);
            System.out.println("Bobs public key  : " + y);

            var ka = modExp(y, a, key.getP());
            var kb = modExp(x, b, key.getP());

            System.out.println("Alices shared secret: " + ka);
            System.out.println("Bobs shared secret: " + kb);
            System.out.println();
        }
    }

    private static int readInt() throws IOException {
        return Integer.parseInt(READER.readLine());
    }
}
