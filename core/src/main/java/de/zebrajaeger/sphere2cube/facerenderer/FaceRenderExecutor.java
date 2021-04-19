package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.EquirectangularImage;
import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.JobExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class FaceRenderExecutor extends JobExecutor {

    public FaceRenderExecutor(ThreadPoolExecutor executor) {
        super(executor);
    }

    public FaceRenderExecutor() {
    }

    public static void renderFace(EquirectangularImage source, WriteableImage target, Face face) throws InterruptedException {
        FaceRenderExecutor executor = new FaceRenderExecutor();
        executor.start(source,target,face);
        executor.shutdown();
    }

    public static Img renderFace(EquirectangularImage source, Face face, int targetEdge) throws InterruptedException {
        Img target = Img.rectangular(targetEdge);
        FaceRenderExecutor executor = new FaceRenderExecutor();
        executor.start(source,target,face);
        executor.shutdown();
        return target;
    }

    public void start( EquirectangularImage source, WriteableImage target, Face face) {
        assert target.getWidth() == target.getHeight();
        int edge = target.getWidth();

        FaceRenderer fr = new FaceRenderer(source);
        for (int y = 0; y < edge; ++y) {
            addJob(new FaceRenderJob(fr, target, face, y));
        }
    }
}
