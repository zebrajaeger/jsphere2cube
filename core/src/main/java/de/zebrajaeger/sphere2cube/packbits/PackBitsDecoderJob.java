package de.zebrajaeger.sphere2cube.packbits;

import de.zebrajaeger.sphere2cube.multithreading.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class PackBitsDecoderJob extends Job {
    private static final Logger LOG = LoggerFactory.getLogger(PackBitsDecoderJob.class);

    private final int index;
    private final byte[] source;
    private final ByteBuffer target;

    public PackBitsDecoderJob(int index, byte[] source, ByteBuffer target) {
        this.index = index;
        this.source = source;
        this.target = target;
    }

    @Override
    public void exec() {
        PackBitsDecoder decoder = new PackBitsDecoder();
        try {
            DecodeResult result = decoder.decode(source, target);
            if (target.capacity() != result.getOutputCount()) {
                LOG.error("Packbit result @ index: '{}' wrong (expected:{} but is {})", index, target.capacity(), result.getOutputCount());
            }
        } catch (Exception e) {
            LOG.error("Packbit index: '{}'", index, e);
        }
    }
}
