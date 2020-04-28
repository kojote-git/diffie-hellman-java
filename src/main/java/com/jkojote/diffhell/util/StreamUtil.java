package com.jkojote.diffhell.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

    public static long readLong(InputStream in) throws IOException {
        var i1 = (long) readInt(in);
        var i2 = (long) readInt(in);

        return i1 | (i2 << 32);
    }

    public static void writeLong(OutputStream out, long number) throws IOException {
        writeInt(out, (int) number);
        writeInt(out, (int) (number >>> 32));
    }

    public static void writeInt(OutputStream out, int number) throws IOException {
        out.write(number);
        out.write(number >>> 8);
        out.write(number >>> 16);
        out.write(number >>> 24);

        out.flush();
    }

    public static int readInt(InputStream in) throws IOException {
        int value = 0;

        for (var i = 0; i < 32; i += 8) {
            value = value | (in.read() << i);
        }

        return value;
    }

}
