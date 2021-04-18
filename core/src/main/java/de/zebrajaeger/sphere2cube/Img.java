package de.zebrajaeger.sphere2cube;

import net.jafama.FastMath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Img implements ReadableImage, WriteableImage {
    private int width;
    private int height;
    private byte[][] r;
    private byte[][] g;
    private byte[][] b;

    public static Img rectangular(int edge) {
        return new Img(edge, edge);
    }

    public Img(int width, int height) {
        this.width = width;
        this.height = height;
        r = new byte[height][];
        g = new byte[height][];
        b = new byte[height][];

        for (int i = 0; i < height; ++i) {
            r[i] = new byte[width];
            g[i] = new byte[width];
            b[i] = new byte[width];
        }
    }

    public Img(File file) throws IOException {
        BufferedImage bi = ImageIO.read(file);
        this.width = bi.getWidth();
        this.height = bi.getHeight();
        r = new byte[height][];
        g = new byte[height][];
        b = new byte[height][];

        for (int y = 0; y < height; ++y) {
            byte[] r_ = new byte[width];
            byte[] g_ = new byte[width];
            byte[] b_ = new byte[width];
            r[y] = r_;
            g[y] = g_;
            b[y] = b_;
            for (int x = 0; x < width; ++x) {
                int c = bi.getRGB(x, y);
                r_[x] = (byte) ((c >> 16) & 0xff);
                g_[x] = (byte) ((c >> 8) & 0xff);
                b_[x] = (byte) (c & 0xff);
            }
        }
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
    public void setPixel(int x, int y, Pixel rgb) {
        r[y][x] = rgb.getRB();
        g[y][x] = rgb.getGB();
        b[y][x] = rgb.getBB();
    }

    @Override
    public void getPixel(int x, int y, Pixel target) {
        target.r = r[y][x] & 0xff;
        target.g = g[y][x] & 0xff;
        target.b = b[y][x] & 0xff;
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return new Pixel(r[y][x], g[y][x], b[y][x]);
    }

    @Override
    public int getRGB(int x, int y) {
        return ((r[y][x] & 0xff) << 16) + ((g[y][x] & 0xff) << 8) + (b[y][x] & 0xff);
    }

    private int bilinearColor(int x1, int x2, int y1, int y2, float ax, float ay, byte[][] colorBuffer) {
        int c1 = colorBuffer[y1][x1] & 0xff;
        int c2 = colorBuffer[y1][x2] & 0xff;
        int c3 = colorBuffer[y2][x1] & 0xff;
        int c4 = colorBuffer[y2][x2] & 0xff;

        float c12 = (c1 * (1 - ax)) + (c2 * ax);
        float c34 = (c3 * (1 - ax)) + (c4 * ax);
        float c1234 = (c12 * (1 - ay)) + (c34 * ay);
        return FastMath.round(c1234);
    }

    public Img scaleBilinear(float factor) {
        return scaleBilinear((int) (width * factor), (int) (height * factor));
    }

    public Img scaleBilinear(int targetWidth, int targetHeight) {
        float fX = (float) (width - 1) / (float) targetWidth;
        float fY = (float) (height - 1) / (float) targetHeight;
        Img img = new Img(targetWidth, targetHeight);
        Pixel pixel = new Pixel();
        for (int y = 0; y < targetHeight; ++y) {
            int y1 = (int) (y * fY);
            int y2 = y1 + 1;
            float ay = (y * fY) - y1;

            for (int x = 0; x < targetWidth; ++x) {
                int x1 = (int) (x * fX);
                int x2 = x1 + 1;
                float ax = (x * fX) - x1;

                pixel.r = bilinearColor(x1, x2, y1, y2, ax, ay, r);
                pixel.g = bilinearColor(x1, x2, y1, y2, ax, ay, g);
                pixel.b = bilinearColor(x1, x2, y1, y2, ax, ay, b);
                img.setPixel(x, y, pixel);
            }
        }
        return img;
    }

    public void copyTo(WriteableImage target, int x, int y) {
        int w = target.getWidth();
        int h = target.getHeight();
        Pixel pixel = new Pixel();
        for (int iy = 0; iy < h; ++iy) {
            for (int ix = 0; ix < w; ++ix) {
                getPixel(x + ix, y + iy, pixel);
                target.setPixel(ix, iy, pixel);
            }
        }
    }
}
