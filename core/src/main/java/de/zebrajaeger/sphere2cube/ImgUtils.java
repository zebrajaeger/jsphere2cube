package de.zebrajaeger.sphere2cube;

import net.jafama.FastMath;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgUtils {
    public static final float DEFAULT_JPEG_QUALITY = 0.8f;

    public static BufferedImage asBufferedImage(@NotNull ReadableImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                bufferedImage.setRGB(x, y, img.getRGB(x, y));
            }
        }
        return bufferedImage;
    }

    public static void save(@NotNull ReadableImage img, @NotNull File file, @Nullable Float quality) throws IOException {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            saveAsJpg(img, file, quality == null ? DEFAULT_JPEG_QUALITY : quality);
        } else if ("png".equals(extension)) {
            saveAsPng(img, file);
        } else {
            throw new UnsupportedOperationException("File extension '" + extension + "' unknown.");
        }
    }

    public static void save(@NotNull ReadableImage img, @NotNull File file, int maxSize) throws IOException {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            float q = 0.5f;
            float step = 0.25f;
            byte[] best = null;
            float bestQuality = -1;
            byte[] bytes;
            for (int i = 0; i < 10 && step > 0.002; ++i) {
                bytes = toJpg(img, q);
                if (bytes.length <= maxSize) {
                    best = bytes;
                    bestQuality = q;
                    q += step;
                } else {
                    q -= step;
                }
                step /= 2f;
            }
            if (best == null) {
                throw new IOException("Could not create a scaled image that meets the specification");
            }
            System.out.println("Choose JPG quality of " + bestQuality);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.write(best, fos);
            }
        } else {
            throw new UnsupportedOperationException("File extension '" + extension + "' not supported with max image size.");
        }
    }

    public static byte[] toJpg(@NotNull ReadableImage img, float quality) throws IOException {
        BufferedImage bufferedImage = asBufferedImage(img);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam writerParams = writer.getDefaultWriteParam();
        writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writerParams.setCompressionQuality(quality);
        writer.setOutput(ImageIO.createImageOutputStream(bos));
        writer.write(null, new IIOImage(bufferedImage, null, null), writerParams);
        return bos.toByteArray();
    }

    public static void saveAsJpg(@NotNull ReadableImage img, @NotNull File file, float quality) throws IOException {
        BufferedImage bufferedImage = asBufferedImage(img);
        try (FileOutputStream os = new FileOutputStream(file)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam writerParams = writer.getDefaultWriteParam();
            writerParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writerParams.setCompressionQuality(quality);
            writer.setOutput(ImageIO.createImageOutputStream(os));
            writer.write(null, new IIOImage(bufferedImage, null, null), writerParams);
        }
    }

    public static byte[] toPng(@NotNull ReadableImage img) throws IOException {
        BufferedImage bufferedImage = asBufferedImage(img);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writerParams = writer.getDefaultWriteParam();
        writer.setOutput(ImageIO.createImageOutputStream(bos));
        writer.write(null, new IIOImage(bufferedImage, null, null), writerParams);
        return bos.toByteArray();
    }

    public static void saveAsPng(@NotNull ReadableImage img, @NotNull File file) throws IOException {
        BufferedImage bufferedImage = asBufferedImage(img);
        try (FileOutputStream os = new FileOutputStream(file)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
            ImageWriteParam writerParams = writer.getDefaultWriteParam();
            writer.setOutput(ImageIO.createImageOutputStream(os));
            writer.write(null, new IIOImage(bufferedImage, null, null), writerParams);
        }
    }

    public static void drawRectangularCross(WriteableImage img, Pixel color) {
        int e = FastMath.min(img.getWidth(), img.getHeight());
        for (int i = 0; i < e; ++i) {
            img.setPixel(i, i, color);
            img.setPixel(e - i - 1, i, color);
        }
    }

    public static void drawDottedRectangularCross(WriteableImage img, Pixel color) {
        int e = FastMath.min(img.getWidth(), img.getHeight());
        for (int i = 0; i < e; ++i) {
            if (i % 8 == 3 || i % 8 == 4) {
                img.setPixel(i, i, color);
                img.setPixel(e - i - 1, i, color);
            }
        }
    }

    public static void drawBorder(WriteableImage img, Pixel color) {
        int h = img.getHeight();
        int w = img.getWidth();
        for (int i = 0; i < w; ++i) {
            img.setPixel(i, 0, color);
            img.setPixel(i, h - 1, color);
        }
        for (int i = 0; i < h; ++i) {
            img.setPixel(0, i, color);
            img.setPixel(w - 1, i, color);
        }
    }

    public static void drawDottedBorder(WriteableImage img, Pixel color) {
        int h = img.getHeight();
        int w = img.getWidth();
        for (int i = 0; i < w; ++i) {
            if (i % 8 == 3 || i % 8 == 4) {
                img.setPixel(i, 0, color);
                img.setPixel(i, h - 1, color);
            }
        }
        for (int i = 0; i < h; ++i) {
            if (i % 8 == 3 || i % 8 == 4) {
                img.setPixel(0, i, color);
                img.setPixel(w - 1, i, color);
            }
        }
    }
}
