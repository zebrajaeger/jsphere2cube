package de.zebrajaeger.sphere2cube.packbits;

import de.zebrajaeger.sphere2cube.PSD;
import de.zebrajaeger.sphere2cube.packbits.DecodeResult;
import de.zebrajaeger.sphere2cube.packbits.PackBitsDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PackBitsDecoderJob implements Runnable {
    private final int index;
    private final byte[] source;
    private final ByteBuffer target;

    public PackBitsDecoderJob(int index, byte[] source, ByteBuffer target) {
        this.index = index;
        this.source = source;
        this.target = target;
    }

    @Override
    public void run() {
        PackBitsDecoder decoder = new PackBitsDecoder();
        try {
            DecodeResult result = decoder.decode(source, target);
            // TODO check size
        } catch (IOException e) {
            System.out.println("FAIL " + index);
            e.printStackTrace();
        }
    }
}
