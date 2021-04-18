package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.packbits.PackBitsDecoder;
import de.zebrajaeger.sphere2cube.packbits.PackBitsDecoderJob;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

public class PSD implements ReadableImage {
    int width;
    int height;
    int channels;
    int version;
    byte[][] lines;

    private PSD() {
    }

    static PSD of(File source) throws IOException, InterruptedException {
        PSD psd = new PSD();
        try (ExtendedInputStream fis = new ExtendedInputStream(new BufferedInputStream(new FileInputStream(source), 1024 * 128))) {
            psd.readHeader(fis);
            psd.readData(fis);
        }
        return psd;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void getPixel(int x, int y, Pixel target) {
        target.setR(lines[y][x]);
        target.setG(lines[y + height][x]);
        target.setB(lines[y + height + height][x]);
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return new Pixel(lines[y][x], lines[y + height][x], lines[y + height + height][x]);
    }

    @Override
    public int getRGB(int x, int y) {
        return ((lines[y][x] & 0xff) << 16) + ((lines[y + height][x] & 0xff) << 8) + (lines[y + height + height][x] & 0xff);
    }

    void readRAWData(ExtendedInputStream dis) throws IOException {
        System.out.println("read RAW Data");
        int lineCount = height * channels;
        lines = new byte[lineCount][];

        for (int x = 0; x < lineCount; ++x) {
            lines[x] = dis.readNewBuffer(width);
        }
    }

    void readRLEData(ExtendedInputStream dis) throws IOException, InterruptedException {
        System.out.println("read RLE Data");
        int lineCount = height * channels;
        long[] lineSizes = new long[lineCount];
        if (version == 1) {
            for (int i = 0; i < lineCount; ++i) {
                lineSizes[i] = dis.readU16();
            }
        } else if (version == 2) {
            for (int i = 0; i < lineCount; ++i) {
                lineSizes[i] = dis.readU32();
            }
        }

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Using threads: " + cores);
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

        lines = new byte[lineCount][];
        for (int i = 0; i < lineCount; ++i) {
            ByteBuffer bb = ByteBuffer.allocate(width);
            lines[i] = bb.array();
            PackBitsDecoderJob job = new PackBitsDecoderJob(this, i, dis.readNewBuffer((int) lineSizes[i]), bb);
            executor.submit(job);
            while (executor.getQueue().size() > 100) {
                Thread.sleep(1);
            }
        }
        while (executor.getActiveCount() > 0) {
            Thread.sleep(1);
        }
        executor.shutdown();
        boolean terminated = executor.awaitTermination(1, TimeUnit.MINUTES);
        if (!terminated) {
            // timeout
            System.err.println("Executor timeout");
        }
    }

    void readData(ExtendedInputStream dis) throws IOException, InterruptedException {
        int compression = dis.readU16();
        System.out.println("Compression: " + compression);
        if (compression == 0) {
            readRAWData(dis);
        } else if (compression == 1) {
            readRLEData(dis);
        }
    }

    void readHeader(ExtendedInputStream dis) throws IOException {

        // Header
        System.out.println("Signature: " + dis.readFixedAsciiString(4));
        version = dis.readU16();
        System.out.println("Version: " + version);
        dis.skip(6);
        channels = dis.readU16();
        System.out.println("Channels: " + channels);
        long h = dis.readU32();
        if (h > Integer.MAX_VALUE) {
            throw new DataInputException("height too large");
        }
        height = (int) h;
        System.out.println("Height: " + height);

        long w = dis.readU32();
        if (w > Integer.MAX_VALUE) {
            throw new DataInputException("width too large");
        }
        width = (int) w;
        System.out.println("Width: " + width);
        System.out.println("Depth: " + dis.readU16());
        System.out.println("ColorMode: " + dis.readU16());

        // Color Mode Data
        long colorModeLength = dis.readU32();
        System.out.println("Color Mode Data length: " + colorModeLength);
        dis.skip(colorModeLength);

        // Image Resources
        long imageResourceLength = dis.readU32();
        System.out.println("Image Resources length: " + imageResourceLength);
        if (imageResourceLength > Integer.MAX_VALUE) {
            throw new DataInputException("imageResourceLength too long");
        }
        byte[] imageResources = dis.readNewBuffer((int) imageResourceLength);
        // TODO parse me

        // Layer and Mask Information
        long layerAndMaskInformationLength;
        if (version == 1) {
            layerAndMaskInformationLength = dis.readU32();
        } else {
            layerAndMaskInformationLength = dis.readU64();
        }
        System.out.println("Layer and Mask Information length: " + layerAndMaskInformationLength);
        dis.skip(layerAndMaskInformationLength);
    }
}
