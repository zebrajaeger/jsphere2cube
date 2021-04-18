package de.zebrajaeger.sphere2cube;

public interface WriteableImage {
    int getWidth();

    int getHeight();

    void setPixel(int x, int y, Pixel rgb);
}
