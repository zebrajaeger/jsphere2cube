package de.zebrajaeger.sphere2cube;

public interface ReadableImage {
    int getWidth();

    int getHeight();

    void getPixel(int x, int y, Pixel target);

    Pixel getPixel(int x, int y);

    int getRGB(int x, int y);
}

