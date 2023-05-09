package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.image.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import de.zebrajaeger.sphere2cube.progress.Progress;

public class FaceRenderExecutor extends MaxJobQueueExecutor {

    public FaceRenderExecutor() {
    }

    public static void renderFace(EquirectangularImage source, WriteableImage target, Face face, Progress progress) throws InterruptedException {
        FaceRenderExecutor executor = new FaceRenderExecutor();
        executor.start(source, target, face, progress);
        executor.shutdown();
    }

    public void start(EquirectangularImage source, WriteableImage target, Face face, Progress progress) {
        assert target.getWidth() == target.getHeight();
        int edge = target.getWidth();

        progress.start(edge);
        FaceRenderer fr = new FaceRenderer(source);
        for (int y = 0; y < edge; ++y) {
            addJob(new FaceRenderJob(fr, target, face, y));
            progress.update(y);
        }
        progress.finish();
    }
}
