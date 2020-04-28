package com.jkojote.diffhell;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Implementation of DES algorithm.
 *
 * Caution for readers. Keep in mind one important detail:
 *
 * It seems that standard DES implementation assumes
 * that you read bits in a number from left to right.
 * It effectively means that the most significant bit
 * is on the position 0 rather than 63 (fuck) which kills readability.
 *
 */
public class Des {
    private static final int[] INITIAL_PERMUTATION = {
        58, 50, 42, 34, 26, 18, 10, 2,
        60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6,
        64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17,  9, 1,
        59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5,
        63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FINAL_PERMUTATION = {
        40, 8, 48, 16, 56, 24, 64, 32,
        39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30,
        37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28,
        35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26,
        33, 1, 41,  9, 49, 17, 57, 25
    };

    private static final int[] S1 = {
        14,  4, 13,  1,  2,  5, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7,
        0,  15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8,
        4,  1,  14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0,
        15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13
    };

    private static final int[] S2 = {
        15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10,
        3,  13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5,
        0,  14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15,
        13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9
    };

    private static final int[] S3 = {
        10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8,
        13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1,
        13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7,
        1,  10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12
    };

    private static final int[] S4 = {
        7,  13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15,
        13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9,
        10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4,
        3,  15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14
    };

    private static final int[] S5 = {
        2,  12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9,
        14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6,
        4,   2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14,
        11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3
    };

    private static final int[] S6 = {
        12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11,
        10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8,
        9,  14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6,
        4,   3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13
    };

    private static final int[] S7 = {
        4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1,
        13, 0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6,
        1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2,
        6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12
    };

    private static final int[] S8 = {
        13, 2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7,
        1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2,
        7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8,
        2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11
    };

    private static final int[] E = {
        32, 1,  2,  3,  4,  5,
        4,  5,  6,  7,  8,  9,
        8,  9, 10, 11, 12, 13,
        12, 13, 14, 15, 16, 17,
        16, 17, 18, 19, 20, 21,
        20, 21, 22, 23, 24, 25,
        24, 25, 26, 27, 28, 29,
        28, 29, 30, 31, 32,  1
    };

    private static final int[] PC1 = {
        57, 49, 41, 33, 25, 17,  9,
        1,  58, 50, 42, 34, 26, 18,
        10,  2, 59, 51, 43, 35, 27,
        19, 11,  3, 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15,
        7,  62, 54, 46, 38, 30, 22,
        14,  6, 61, 53, 45, 37, 29,
        21, 13,  5, 28, 20, 12,  4
    };

    private static final int[] PC2 = {
        14, 17, 11, 24,  1,  5,
        3,  28, 15,  6, 21, 10,
        23, 19, 12,  4, 26,  8,
        16,  7, 27, 20, 13,  2,
        41, 52, 31, 37, 47, 55,
        30, 40, 51, 45, 33, 48,
        44, 49, 39, 56, 34, 53,
        46, 42, 50, 36, 29, 32
    };

    private static final int[] P = {
        16,  7, 20, 21,
        29, 12, 28, 17,
        1,  15, 23, 26,
        5,  18, 31, 10,
        2,   8, 24, 14,
        32, 27,  3,  9,
        19, 13, 30,  6,
        22, 11,  4, 25
    };
    private static final int[] SHIFTS = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

    private static final long[] WEAK_KEYS = {
        0x0101010101010101L,
        0xFEFEFEFEFEFEFEFEL,
        0xE0E0E0E0F1F1F1F1L,
        0x1F1F1F1F0E0E0E0EL,

        0x0000000000000000L,
        0xFFFFFFFFFFFFFFFFL,
        0xE1E1E1E1F0F0F0F0L,
        0x1E1E1E1E0F0F0F0FL,

        // semi-weak keys

        0x011F011F010E010EL,
        0x1F011F010E010E01L,

        0x01E001E001F101F1L,
        0xE001E001F101F101L,

        0x01FE01FE01FE01FEL,
        0xFE01FE01FE01FE01L,

        0x1FE01FE00EF10EF1L,
        0xE01FE01FF10EF10EL,

        0x1FFE1FFE0EFE0EFEL,
        0xFE1FFE1FFE0EFE0EL,

        0xE0FEE0FEF1FEF1FEL,
        0xFEE0FEE0FEF1FEF1L
    };

    private static final long MASK_32_BITS = 0xffffffffL;
    private static final long MASK_28_BITS = 0xfffffffL;
    private static final long MASK_8_BITS = 0xffL;
    private static final long MASK_6_BITS = 0x3fL;
    private static final long MASK_4_BITS = 0xfL;
    private static final Charset CHARSET = Charset.defaultCharset();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public long nextKey() {
        while (true) {
            long key = SECURE_RANDOM.nextLong();

            if (!keyIsWeak(key)) {
                return key;
            }
        }
    }

    private boolean keyIsWeak(long key) {
        for (long weakKey : WEAK_KEYS) {
            if (key == weakKey) {
                return true;
            }
        }
        return false;
    }

    public String encryptString(String plainString, long key) {
        byte[] encryptedBytes = encryptBytes(plainString.getBytes(CHARSET), key);
        return new String(BASE64_ENCODER.encode(encryptedBytes), CHARSET);
    }

    public String decryptString(String encryptedString, long key) {
        // SOME UNKNOWN MAGIC WITH BASE64 I DON'T REALLY KNOW
        // BUT THIS WAY ENCRYPTION/DECRYPTION OF RAW BYTES WORKS FINE


        byte[] decodedBytes = BASE64_DECODER.decode(encryptedString.getBytes(CHARSET));
        byte[] decryptedBytes = decryptBytes(decodedBytes, key);
        return new String(decryptedBytes, CHARSET);
    }

    public byte[] encryptBytes(byte[] bytes, long key) {
        return cipherBytes(bytes, key, true);
    }

    public byte[] decryptBytes(byte[] bytes, long key) {
        byte[] decryptedBytes = cipherBytes(bytes, key, false);
        return removeTrailingZeros(decryptedBytes);
    }

    private byte[] cipherBytes(byte[] input, long key, boolean encrypt) {
        byte[] plainBytes = padWithZeros(input);
        byte[] cipheredBytes = new byte[plainBytes.length];

        for (int i = 0; i < plainBytes.length; i += 8) {
            long plainBlock = 0;
            for (int j = 0; j < 8; j++) {
                int unsignedByte = plainBytes[i + j] & 0xFF;
                if (j == 7) {
                    plainBlock = (plainBlock | (unsignedByte & MASK_8_BITS));
                } else {
                    plainBlock = (plainBlock | (unsignedByte & MASK_8_BITS)) << 8;
                }
            }
            long cipheredBlock;
            if (encrypt) {
                cipheredBlock = encryptBlock(plainBlock, key);
            } else {
                cipheredBlock = decryptBlock(plainBlock, key);
            }
            for (int j = 0; j < 8; j++) {
                int unsignedByte = ((int) (cipheredBlock >>> (56 - j * 8))) & 0xFF;
                cipheredBytes[i + j] = (byte) (unsignedByte & MASK_8_BITS);
            }
        }
        return cipheredBytes;
    }

    private byte[] removeTrailingZeros(byte[] bytes) {
        int newLength = bytes.length;
        for (; newLength > 0; newLength--) {
            if (bytes[newLength - 1] != 0) {
                break;
            }
        }
        return Arrays.copyOf(bytes, newLength);
    }

    private byte[] padWithZeros(byte[] inputBytes) {
        byte[] resultArray;

        if (inputBytes.length % 8 != 0) {
            int diff = 8 - (inputBytes.length % 8);
            resultArray = new byte[inputBytes.length + diff];
            System.arraycopy(inputBytes, 0, resultArray, 0, inputBytes.length);
            return resultArray;
        } else {
            return inputBytes;
        }
    }

    public long encryptBlock(long block, long key) {
        block = doInitialPermutation(block);

        long left = block >>> 32;
        long right = block & MASK_32_BITS;
        long[] keys = genKeys(key);

        for (int i = 0; i < 16; i++) {
            long prevLeft = left;
            left = right;
            right = prevLeft ^ f(right, keys[i]);
        }

        return doFinalPermutation((left << 32) | right);
    }

    public long decryptBlock(long block, long key) {
        block = doInitialPermutation(block);

        long left = block >>> 32;
        long right = block & MASK_32_BITS;
        long[] keys = genKeys(key);

        for (int i = 0; i < 16; i++) {
            long prevRight = right;
            right = left;
            left = prevRight ^ f(left, keys[15 - i]);
        }

        return doFinalPermutation((left << 32) | right);
    }

    private double calculateEntropyOfOne(long block) {
        int ones = 0;
        for (int i = 0; i < 64; i++) {
            if (getBit(block, i) == 1) {
                ones++;
            }
        }
        double probabilityOfOne = ones / 64.0;
        return -log2(probabilityOfOne) * probabilityOfOne;
    }

    private double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    private long f(long block32bits, long key) {
        // move rightmost 32 bits to the left
        // expand to 48 bits and move 16 bits back to the right
        long block48bits = expand(block32bits);

        block48bits = block48bits ^ key; // LR
        long b1 = (block48bits >>> 42) & MASK_6_BITS;
        long b2 = (block48bits >>> 36) & MASK_6_BITS;
        long b3 = (block48bits >>> 30) & MASK_6_BITS;
        long b4 = (block48bits >>> 24) & MASK_6_BITS;
        long b5 = (block48bits >>> 18) & MASK_6_BITS;
        long b6 = (block48bits >>> 12) & MASK_6_BITS;
        long b7 = (block48bits >>> 6) & MASK_6_BITS;
        long b8 = (block48bits) & MASK_6_BITS;

        long encryptedBlock32bits = (
            (substitute(b1, Des.S1) << 28) |
                (substitute(b2, Des.S2) << 24) |
                (substitute(b3, Des.S3) << 20) |
                (substitute(b4, Des.S4) << 16) |
                (substitute(b5, Des.S5) << 12) |
                (substitute(b6, Des.S6) << 8) |
                (substitute(b7, Des.S7) << 4) |
                (substitute(b8, Des.S8))
        );

        return permute32BitBlock(encryptedBlock32bits, Des.P);
    }

    private long[] genKeys(long key64Bits) {
        long[] keys = new long[16];

        // after permutation there is only 56 leftmost bits.
        // move them to the right to make things work.
        long key56Bits = permute(key64Bits, Des.PC1) >>> 8;
        long c = key56Bits >>> 28;
        long d = key56Bits & MASK_28_BITS;

        for (int i = 0; i < 16; i++) {
            c = lcshift28BitValue(c, Des.SHIFTS[i]);
            d = lcshift28BitValue(d, Des.SHIFTS[i]);
            keys[i] = permute(((c << 28)| d) << 8, Des.PC2) >>> 16;
        }
        return keys;
    }

    // left circular shift of 28 bit value
    private long lcshift28BitValue(long value28bit, int shift) {
        long leftMostPart = value28bit >>> (28 - shift);
        long rightMostPart = value28bit & (MASK_28_BITS >>> shift);
        return (rightMostPart << shift) | leftMostPart;
    }

    private long substitute(long box, int[] s) {
        long firstBit = getBit(box, 5);
        long lastBit = getBit(box, 0);

        long column = (box >>> 1) & MASK_4_BITS;
        long row = setBit(0, 1, firstBit) | setBit(0, 0, lastBit);

        return s[(int) (row * 4 + column)];
    }

    private long expand(long block32bits) {
        // move rightmost 32 bits to the left
        block32bits <<= 32;

        // expand leftmost 32 bits to 48 bits
        long block48bits = 0;
        for (int i = 0; i < 48; i++) {
            long bit = getBit(block32bits, 63 - (Des.E[i] - 1)); // LR
            block48bits = setBit(block48bits, 63 - i, bit); // LR
        }

        // move leftmost 48 bits to the right
        return block48bits >>> 16;
    }

    private long doInitialPermutation(long block) {
        return permute(block, Des.INITIAL_PERMUTATION);
    }

    private long doFinalPermutation(long block) {
        return permute(block, Des.FINAL_PERMUTATION);
    }

    private long permute32BitBlock(long block, int[] table) {
        return permute(block << 32, table) >>> 32;
    }

    private long permute(long block, int[] table) {
        long value = 0;
        for (int i = 0; i < table.length; i++) {
            long bit = getBit(block, 63 - (table[i] - 1)); // LR
            value = setBit(value, 63 - i, bit); // LR
        }
        return value;
    }

    /*
     * Returns bit in block at specified position.
     * Position is a number from 0 to 63.
     * 0  - is the position of the rightmost bit.
     * 63 - is the position of the leftmost bit.
     */
    private long getBit(long block, int position) {
        return (block >>> position) & 1;
    }

    /*
     * Sets bit in block at specified position.
     * Position is a number from 0 to 63.
     * 0  - is the position of the rightmost bit.
     * 63 - is the position of the leftmost bit.
     */
    private long setBit(long block, int position, long bit) {
        if (bit == 1) {
            return block | (1L << position);
        } else if (bit == 0) {
            return block & (~(1L << position));
        }
        throw new RuntimeException("fuck off");
    }
}
