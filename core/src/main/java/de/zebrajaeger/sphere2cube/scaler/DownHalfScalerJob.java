package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Pixel;
import de.zebrajaeger.sphere2cube.ReadableImage;
import de.zebrajaeger.sphere2cube.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.Job;
import net.jafama.FastMath;

public class DownHalfScalerJob extends Job {
    private final ReadableImage source;
    private final WriteableImage target;
    private final int lineToProcess;

    public DownHalfScalerJob(ReadableImage source, WriteableImage target, int lineToProcess) {
        this.source = source;
        this.target = target;
        this.lineToProcess = lineToProcess;
    }

    @Override
    public void exec() {
        Pixel p1 = new Pixel();
        Pixel p2 = new Pixel();
        Pixel p3 = new Pixel();
        Pixel p4 = new Pixel();
        Pixel pt = new Pixel();

        int iy = lineToProcess;
        int targetWidth = target.getWidth();

        for (int ix = 0; ix < targetWidth; ++ix) {
            int x1 = ix << 1;
            int x2 = x1 + 1;
            int y1 = iy << 1;
            int y2 = y1 + 1;

            source.getPixel(x1, y1, p1);
            source.getPixel(x1, y2, p2);
            source.getPixel(x2, y1, p3);
            source.getPixel(x2, y2, p4);

            pt.r = FastMath.round((p1.r + p2.r + p3.r + p4.r) / 4f);
            pt.g = FastMath.round((p1.g + p2.g + p3.g + p4.g) / 4f);
            pt.b = FastMath.round((p1.b + p2.b + p3.b + p4.b) / 4f);

            target.setPixel(ix, iy, pt);
        }
    }
}
