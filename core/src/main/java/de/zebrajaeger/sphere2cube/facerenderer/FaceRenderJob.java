package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.image.Pixel;
import de.zebrajaeger.sphere2cube.image.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.Job;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FaceRenderJob extends Job {
    private final FaceRenderer faceRenderer;
    private final Face face;
    private final WriteableImage target;
    private final int targetEdge;
    private final int lineToRender;

    public FaceRenderJob(FaceRenderer faceRenderer, WriteableImage target, Face face, int lineToRender) {
        this.faceRenderer = faceRenderer;
        this.target = target;
        this.face = face;
        this.lineToRender = lineToRender;
        targetEdge = target.getWidth();
    }

    @Override
    public void exec() {
        try {
            Pixel pixel = new Pixel();
            for (int x = 0; x < targetEdge; ++x) {
                boolean invertY = face == Face.UP;
                boolean invertX = false;
                faceRenderer.calculatePixel(invertX, invertY, x, lineToRender, face, targetEdge, pixel);
                target.setPixel(x, lineToRender, pixel);
            }
        } catch (Throwable t) {
            log.error("Face renderer", t);
        }
    }
}
