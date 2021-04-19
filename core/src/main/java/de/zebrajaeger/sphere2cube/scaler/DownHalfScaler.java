package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.ReadableImage;
import de.zebrajaeger.sphere2cube.multithreading.JobExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class DownHalfScaler extends JobExecutor {

    private Img target;

    public static Img scale(ReadableImage source) throws InterruptedException {
        DownHalfScaler downHalfScaler = new DownHalfScaler();
        downHalfScaler.start(source);
        downHalfScaler.shutdown();
        return downHalfScaler.getTarget();
    }

    public static Img scale(ThreadPoolExecutor executor, ReadableImage source) throws InterruptedException {
        DownHalfScaler downHalfScaler = new DownHalfScaler(executor);
        downHalfScaler.start(source);
        downHalfScaler.shutdown();
        return downHalfScaler.getTarget();
    }

    public DownHalfScaler(ThreadPoolExecutor executor) {
        super(executor);
    }

    public DownHalfScaler() {
    }

    public void start(ReadableImage source) throws InterruptedException {
        int targetWidth = source.getWidth() / 2;
        int targetHeight = source.getHeight() / 2;

        target = new Img(targetWidth, targetHeight);
        for (int y = 0; y < targetHeight; ++y) {
            addJob(new DownHalfScalerJob(source, target, y));
        }
    }

    public Img getTarget() {
        return target;
    }
}
