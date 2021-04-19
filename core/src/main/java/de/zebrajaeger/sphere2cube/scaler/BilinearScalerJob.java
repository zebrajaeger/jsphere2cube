package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Pixel;
import de.zebrajaeger.sphere2cube.ReadableImage;
import de.zebrajaeger.sphere2cube.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.Job;

public class BilinearScalerJob extends Job {
    private final ReadableImage source;
    private final WriteableImage target;
    private final int lineToProcess;

    public BilinearScalerJob(ReadableImage source, WriteableImage target, int lineToProcess) {
        this.source = source;
        this.target = target;
        this.lineToProcess = lineToProcess;
    }

    @Override
    public void exec() {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        int targetWidth = target.getWidth();
        int targetHeight = target.getHeight();


        float tx = (float) sourceWidth / (float) (targetWidth - 1);
        float ty = (float) sourceHeight / (float) (targetHeight - 1);
        Pixel p1 = new Pixel();
        Pixel p2 = new Pixel();
        Pixel p3 = new Pixel();
        Pixel p4 = new Pixel();
        Pixel pt = new Pixel();

        int iy = lineToProcess;
        for (int ix = 0; ix < targetWidth; ++ix) {
            int x = (int) (tx * ix);
            int y = (int) (ty * iy);
            float xDiff = (tx * ix) - x;
            float yDiff = (ty * iy) - y;

            source.getPixel(x, y, p1);
            source.getPixel(x + 1, y, p2);
            source.getPixel(x, y + 1, p3);
            source.getPixel(x + 1, y + 1, p4);

            float r = p1.r * (1 - xDiff) * (1 - yDiff)
                    + p2.r * (1 - yDiff) * (xDiff)
                    + p3.r * (yDiff) * (1 - xDiff)
                    + p4.r * (yDiff) * (xDiff);
            float g = p1.g * (1 - xDiff) * (1 - yDiff)
                    + p2.g * (1 - yDiff) * (xDiff)
                    + p3.g * (yDiff) * (1 - xDiff)
                    + p4.g * (yDiff) * (xDiff);
            float b = p1.b * (1 - xDiff) * (1 - yDiff)
                    + p2.b * (1 - yDiff) * (xDiff)
                    + p3.b * (yDiff) * (1 - xDiff)
                    + p4.b * (yDiff) * (xDiff);
            pt.r = Math.round(r);
            pt.g = Math.round(g);
            pt.b = Math.round(b);

            target.setPixel(ix, iy, pt);
        }

    }
}

//        // 2 -> 1  / f=2
//        float fX = (float) (sourceWidth) / (float) targetWidth;
//        float fY = (float) (sourceHeight) / (float) targetHeight;
//        int y1 = (int) (lineToProcess * fY);
//        int y2 = y1 + 1;
//        float ay = (lineToProcess * fY) - y1;
//        Pixel p1 = new Pixel();
//        Pixel p2 = new Pixel();
//        Pixel p3 = new Pixel();
//        Pixel p4 = new Pixel();
//        Pixel pt = new Pixel();
//
//        // in:64
//        // out:32
//        // fx = 63/32 = 1,96875
//        // x = 31 -> x1 = 61,03125 = 61, x2 = 62
//
//        for (int x = 0; x < targetWidth; ++x) {
//            int x1 = (int) (x * fX);
//            int x2 = x1 + 1;
//            float ax = (x * fX) - x1;
//
//            source.getPixel(x1, y1, p1);
//            source.getPixel(x2, y1, p2);
//            source.getPixel(x1, y2, p3);
//            source.getPixel(x2, y2, p4);
//
//            // TODO here is something wrong...
//            pt.r = (p1.r + p2.r+p3.r+p4.r)/4;
//            pt.g = (p1.g + p2.g+p3.g+p4.g)/4;
//            pt.b = (p1.b + p2.b+p3.b+p4.b)/4;
//
////            pt.r = FastMath.round((((p1.r * (1 - ax)) + (p2.r * ax)) * (1 - ay)) + (((p3.r * (1 - ax)) + (p4.r * ax)) * ay));
////            pt.g = FastMath.round((((p1.g * (1 - ax)) + (p2.g * ax)) * (1 - ay)) + (((p3.g * (1 - ax)) + (p4.g * ax)) * ay));
////            pt.b = FastMath.round((((p1.b * (1 - ax)) + (p2.b * ax)) * (1 - ay)) + (((p3.b * (1 - ax)) + (p4.b * ax)) * ay));
//
//            target.setPixel(x, lineToProcess, pt);
////            target.setPixel(x, lineToProcess, p4);
//        }
//    }
//}
