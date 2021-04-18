package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.ReadableImage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownHalfScaler {

    public Img scale(ReadableImage source) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

        int targetWidth = source.getWidth() / 2;
        int targetHeight = source.getHeight() / 2;

        Img target = new Img(targetWidth, targetHeight);
        for (int y = 0; y < targetHeight; ++y) {
            executor.submit(new DownHalfScalerJob(source, target, y));
        }
        executor.shutdown();
        executor.awaitTermination(365, TimeUnit.DAYS);
        return target;
    }
}
