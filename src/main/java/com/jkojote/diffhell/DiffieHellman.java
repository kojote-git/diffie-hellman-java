package com.jkojote.diffhell;

import static com.jkojote.diffhell.MathUtil.findPrimitiveRoot;
import static com.jkojote.diffhell.MathUtil.nextPrime;

public class DiffieHellman {

    public static Key generateKey() {
        var p = -1;
        var g = -1;

        while ((p == -1) || (g == -1)) {
            p = nextPrime(0xFF);
            g = findPrimitiveRoot(p);
        }

        return new Key(p, g);
    }

}