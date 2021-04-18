package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.EquirectangularImage;
import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.WriteableImage;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FaceRenderExecutor {
    public static Img renderFace(EquirectangularImage source, Face face, int edge) throws InterruptedException {
        Img img = new Img(edge, edge);
        renderFace(source, img, face);
        return img;
    }

    public static void renderFace(EquirectangularImage source, WriteableImage target, Face face) throws InterruptedException {
        assert target.getWidth() == target.getHeight();
        int edge = target.getWidth();

        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

        FaceRenderer fr = new FaceRenderer(source);
        for (int y = 0; y < edge; ++y) {
            executor.submit(new FaceRenderJob(fr, target, face, y));
        }
        executor.shutdown();
        executor.awaitTermination(365, TimeUnit.DAYS);
    }
}
