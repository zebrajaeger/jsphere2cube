package de.zebrajaeger.sphere2cube.scaler;

import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.ReadableImage;
import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import de.zebrajaeger.sphere2cube.progress.Progress;

public class DownHalfScaler extends MaxJobQueueExecutor {

    private Img target;

    public static Img scale(ReadableImage source) throws InterruptedException {
        return scale(source, Progress.DUMMY);
    }

    public static Img scale(ReadableImage source, Progress progress) throws InterruptedException {
        DownHalfScaler downHalfScaler = new DownHalfScaler();
        downHalfScaler.start(source, progress);
        downHalfScaler.shutdown();
        return downHalfScaler.getTarget();
    }

    public DownHalfScaler() {
        super();
    }

    public void start(ReadableImage source, Progress progress) throws InterruptedException {
        int targetWidth = source.getWidth() / 2;
        int targetHeight = source.getHeight() / 2;

        progress.start(targetHeight);
        target = new Img(targetWidth, targetHeight);
        for (int y = 0; y < targetHeight; ++y) {
            addJob(new DownHalfScalerJob(source, target, y));
            progress.update(y);
        }
        progress.finish();
    }

    public Img getTarget() {
        return target;
    }
}
