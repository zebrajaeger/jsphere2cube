package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.Pixel;
import de.zebrajaeger.sphere2cube.WriteableImage;

public class FaceRenderJob implements Runnable {
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
                faceRenderer.calculatePixel(false, false, x, lineToRender, face, targetEdge, pixel);
                target.setPixel(x, lineToRender, pixel);
            }
        } catch (Throwable t) {
            System.out.println(t);
        }
    }
}
