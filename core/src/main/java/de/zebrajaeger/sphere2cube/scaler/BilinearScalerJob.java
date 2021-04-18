package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Pixel;
import de.zebrajaeger.sphere2cube.ReadableImage;
import de.zebrajaeger.sphere2cube.WriteableImage;
import net.jafama.FastMath;

public class BilinearScalerJob implements Runnable {
    private ReadableImage source;
    private WriteableImage target;
    private int lineToProcess;

    public BilinearScalerJob(ReadableImage source, WriteableImage target, int lineToProcess) {
        this.source = source;
        this.target = target;
        this.lineToProcess = lineToProcess;
    }

    @Override
    public void run() {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();

        float fX = (float) (sourceWidth - 1) / (float) targetWidth;
        float fY = (float) (sourceHeight - 1) / (float) targetHeight;
        int y1 = (int) (lineToProcess * fY);
        int y2 = y1 + 1;
        float ay = (lineToProcess * fY) - y1;
        Pixel p1 = new Pixel();
        Pixel p2 = new Pixel();
        Pixel p3 = new Pixel();
        Pixel p4 = new Pixel();
        Pixel pt = new Pixel();

        for (int x = 0; x < targetWidth; ++x) {
            int x1 = (int) (x * fX);
            int x2 = x1 + 1;
            float ax = (x * fX) - x1;

            source.getPixel(x1, y1, p1);
            source.getPixel(x2, y1, p2);
            source.getPixel(x1, y2, p3);
            source.getPixel(x2, y2, p4);

            pt.r = FastMath.round((((p1.r * (1 - ax)) + (p2.r * ax)) * (1 - ay)) + (((p3.r * (1 - ax)) + (p4.r * ax)) * ay));
            pt.g = FastMath.round((((p1.g * (1 - ax)) + (p2.g * ax)) * (1 - ay)) + (((p3.g * (1 - ax)) + (p4.g * ax)) * ay));
            pt.b = FastMath.round((((p1.b * (1 - ax)) + (p2.b * ax)) * (1 - ay)) + (((p3.b * (1 - ax)) + (p4.b * ax)) * ay));

            target.setPixel(x, lineToProcess, pt);
        }
    }
}
