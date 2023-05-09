package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.image.ReadableImage;
import de.zebrajaeger.sphere2cube.multithreading.JobExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class BilinearScaler extends JobExecutor {
    private Img target;

    public static Img scale(ReadableImage source, int targetWidth, int targetHeight) throws InterruptedException {
        BilinearScaler bilinearScaler = new BilinearScaler();
        bilinearScaler.start(source, targetWidth, targetHeight);
        bilinearScaler.shutdown();
        return bilinearScaler.getTarget();
    }

    public static Img scale(ThreadPoolExecutor executor, ReadableImage source, int targetWidth, int targetHeight) throws InterruptedException {
        BilinearScaler bilinearScaler = new BilinearScaler(executor);
        bilinearScaler.start(source, targetWidth, targetHeight);
        bilinearScaler.shutdown();
        return bilinearScaler.getTarget();
    }

    public BilinearScaler(ThreadPoolExecutor executor) {
        super(executor);
    }

    public BilinearScaler() {
    }

    public Img getTarget() {
        return target;
    }

    public void start(ReadableImage source, int targetWidth, int targetHeight) throws InterruptedException {
        target = new Img(targetWidth, targetHeight);
        for (int y = 0; y < targetHeight; ++y) {
            addJob(new BilinearScalerJob(source, target, y));
        }
    }
}
