package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.ReadableImage;

public interface Scaler {
    Img scale(ReadableImage source, int targetWidth, int targetHeight) throws InterruptedException;
}
