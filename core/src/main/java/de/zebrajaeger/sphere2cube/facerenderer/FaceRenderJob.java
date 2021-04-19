package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.Pixel;
import de.zebrajaeger.sphere2cube.WriteableImage;
import de.zebrajaeger.sphere2cube.multithreading.Job;

public class FaceRenderJob implements Job {
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
    public void run() {
        try {
            Pixel pixel = new Pixel();
            for (int x = 0; x < targetEdge; ++x) {
                boolean invertY = face == Face.UP;
                boolean invertX = false;
                faceRenderer.calculatePixel(invertX, invertY, x, lineToRender, face, targetEdge, pixel);
                target.setPixel(x, lineToRender, pixel);
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }
}
