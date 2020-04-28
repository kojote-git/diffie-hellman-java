package com.jkojote.diffhell.util;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class MathUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static Set<Integer> PRIMES = new HashSet<>();

    public static int nextPrime(int max) {
        var prime = Math.abs(nextInt(max));
        while (!isPrime(prime)) {
            prime = Math.abs(nextInt(max));
        }
        return prime;
    }

    public static int nextInt(int max) {
        return RANDOM.nextInt() % max;
    }

    public static int nextInt() {
        return nextInt(Integer.MAX_VALUE);
    }

    public static boolean isPrime(int number) {
        if (PRIMES.contains(number)) {
            return true;
        }
        var sqrt = Math.sqrt(number);
        for (var i = 2; i <= sqrt; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        PRIMES.add(number);
        return true;
    }

    public static int findPrimitiveRoot(int primeNumber) {
        for (var i = 1; i < primeNumber; i++) {
            if (isPrimitiveRoot(i, primeNumber)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isPrimitiveRoot(int possibleRoot, int primeNumber) {
        var congruentsFound = new boolean[primeNumber - 1];
        for (var i = 1; i < primeNumber; i++) {
            var congruent = (int) modExp(possibleRoot, i, primeNumber);
            congruentsFound[congruent - 1] = true;
        }
        var result = true;
        for (var congruentFound : congruentsFound) {
            result = result && congruentFound;
        }
        return result;
    }

    public static long modExp(long n, long exp, long mod) {
        long i  = 1;
        for (var k = 0; k < exp; k++) {
            i = (i * n) % mod;
        }
        return i;
    }
}
