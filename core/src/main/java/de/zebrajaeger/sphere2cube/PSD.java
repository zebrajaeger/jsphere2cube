package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import de.zebrajaeger.sphere2cube.packbits.PackBitsDecoderJob;
import de.zebrajaeger.sphere2cube.progress.Progress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PSD implements ReadableImage {
    private int width;
    private int height;
    private int channels;
    private int version;
    private ByteBuffer[] lines;

    private PSD() {
    }

    static PSD of(File source, Progress progress) throws IOException, InterruptedException {
        PSD psd = new PSD();
        try (ExtendedInputStream fis = new ExtendedInputStream(new BufferedInputStream(new FileInputStream(source), 1024 * 128))) {
            psd.readHeader(fis);
            psd.readData(fis, progress);
        }
        return psd;
    }

    static PSD of(File source) throws IOException, InterruptedException {
        PSD psd = new PSD();
        try (ExtendedInputStream fis = new ExtendedInputStream(new BufferedInputStream(new FileInputStream(source), 1024 * 128))) {
            psd.readHeader(fis);
            psd.readData(fis, Progress.DUMMY);
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
        target.setR(lines[y].get(x));
        target.setG(lines[y + height].get(x));
        target.setB(lines[y + height + height].get(x));
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return new Pixel(lines[y].get(x), lines[y + height].get(x), lines[y + height + height].get(x));
    }

    @Override
    public int getRGB(int x, int y) {
        return ((lines[y].get(x) & 0xff) << 16) + ((lines[y + height].get(x) & 0xff) << 8) + (lines[y + height + height].get(x) & 0xff);
    }

    public static String toImageSize(long pixelCount) {
        if (pixelCount < 1000) {
            return String.format("%d px", pixelCount);
        } else if (pixelCount < 1000 * 1000) {
            return String.format("%.2f Kpx", pixelCount / (float) 1000);
        } else if (pixelCount < 1000 * 1000 * 1000) {
            return String.format("%.2f Mpx", pixelCount / (float) (1000 * 1000));
        } else {
            return String.format("%.2f Gpx", pixelCount / (float) (1000 * 1000 * 1000));
        }
    }

    void readRAWData(ExtendedInputStream dis, Progress progress) throws IOException {
        System.out.println("read RAW Data");
        int lineCount = height * channels;
        lines = new ByteBuffer[lineCount];

        progress.start(lineCount);
        for (int i = 0; i < lineCount; ++i) {
            lines[i] = dis.readDirectByteBuffer(width);
            progress.update(i);
        }
        progress.finish();
    }

    void readRLEData(ExtendedInputStream extendedInputStream, Progress progress) throws IOException, InterruptedException {
        System.out.println("read RLE Data");
        int lineCount = height * channels;
        long[] lineSizes = new long[lineCount];
        if (version == 1) {
            for (int i = 0; i < lineCount; ++i) {
                lineSizes[i] = extendedInputStream.readU16();
            }
        } else if (version == 2) {
            for (int i = 0; i < lineCount; ++i) {
                lineSizes[i] = extendedInputStream.readU32();
            }
        }

        progress.start(lineCount);
        MaxJobQueueExecutor executor = MaxJobQueueExecutor.withMaxQueueSize(50);
        lines = new ByteBuffer[lineCount];
        for (int i = 0; i < lineCount; ++i) {
            ByteBuffer bb = ByteBuffer.allocateDirect(width);
            lines[i] = bb;
            PackBitsDecoderJob job = new PackBitsDecoderJob(i, extendedInputStream.readNewBuffer((int) lineSizes[i]), bb);
            executor.addJob(job);
            progress.update(i);
        }
        executor.shutdown();
        progress.finish();
    }

    void readData(ExtendedInputStream dis, Progress progress) throws IOException, InterruptedException {
        int compression = dis.readU16();
        System.out.println("Compression: " + compression);
        if (compression == 0) {
            readRAWData(dis, progress);
        } else if (compression == 1) {
            readRLEData(dis, progress);
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
        System.out.println("Size: " + toImageSize((long) width * height));
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
