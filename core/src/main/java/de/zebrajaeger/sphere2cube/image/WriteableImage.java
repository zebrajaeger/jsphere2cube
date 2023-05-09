package de.zebrajaeger.sphere2cube.image;

import de.zebrajaeger.sphere2cube.image.Pixel;

public interface WriteableImage {
    int getWidth();

    int getHeight();

    void setPixel(int x, int y, Pixel rgb);
}
