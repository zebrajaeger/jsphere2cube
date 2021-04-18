package de.zebrajaeger.sphere2cube;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ExtendedInputStream extends DataInputStream {

    public ExtendedInputStream(@org.jetbrains.annotations.NotNull InputStream in) {
        super(in);
    }

    byte[] readNewBuffer(int size) throws IOException {
        byte[] buffer = new byte[size];
        int l = read(buffer);
        if (size != l) {
            throw new DataInputException("Wrong buffer size");
        }
        return buffer;
    }

    ByteBuffer readByteBuffer(int size) throws IOException {
        return ByteBuffer.wrap(readNewBuffer(size));
    }

    String readFixedAsciiString(int size) throws IOException {
        return new String(readNewBuffer(size), StandardCharsets.US_ASCII);
    }

    public int readU16() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2);
    }

    public long readU32() throws IOException {
        long ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    public long readU64() throws IOException {
        long ch1 = in.read();
        long ch2 = in.read();
        long ch3 = in.read();
        long ch4 = in.read();
        long ch5 = in.read();
        int ch6 = in.read();
        int ch7 = in.read();
        int ch8 = in.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0)
            throw new EOFException();
        return (ch1 << 56) + (ch2 << 48) + (ch3 << 40) + (ch4 << 32) + (ch5 << 24) + (ch6 << 16) + (ch7 << 8) + ch8;
    }

}
