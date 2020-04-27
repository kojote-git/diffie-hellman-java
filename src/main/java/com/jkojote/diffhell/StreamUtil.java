package com.jkojote.diffhell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

    public static long readLong(InputStream in) throws IOException {
        return ((long) readInt(in)) | (((long) readInt(in)) << 32);
    }

    public static void writeLong(OutputStream out, long number) throws IOException {
        writeInt(out, (int) number);
        writeInt(out, (int) (number >> 32));
    }

    public static void writeInt(OutputStream out, int number) throws IOException {
        out.write(number);
        out.write(number >> 8);
        out.write(number >> 16);
        out.write(number >> 24);
    }

    public static int readInt(InputStream in) throws IOException {
        int value = 0;

        for (var i = 0; i < 32; i++) {
            value = (value << i) | in.read();
        }

        return value;
    }

}
