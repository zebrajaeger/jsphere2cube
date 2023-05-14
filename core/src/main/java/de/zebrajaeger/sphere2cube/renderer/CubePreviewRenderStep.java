package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.image.CubeMapImage;
import de.zebrajaeger.sphere2cube.progress.Progress;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.ImgUtils;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class CubePreviewRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getPreviewsConfig().getCubeMapPreview().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();
    log.info("Render preview cubemap");

    File targetFile = new File(renderContext.getOutputFolder(), "preview_cube.jpg");
    try {
      FileUtils.forceMkdirParent(targetFile);
    } catch (IOException e) {
      throw new RenderException("Could not create folder for cube preview image", e);
    }

    int previewCubeEdge = renderContext.getConfig().getPreviewsConfig().getCubeMapPreview()
        .getEdge();
    CubeMapImage cubeMapImage = new CubeMapImage(previewCubeEdge);
    try {
      for (Face face : Face.values()) {
        FaceRenderExecutor.renderFace(renderContext.getSource(), cubeMapImage.getFaceImg(face),
            face, Progress.DUMMY);
      }
    } catch (InterruptedException e) {
      throw new RenderException("Could not render face for cube preview image ", e);
    }
    log.info("Rendered preview cubemap in {}", chronograph.stop());

    log.info("Save preview cube: '{}'", targetFile.getAbsolutePath());
    chronograph = Chronograph.start();
    try {
      ImgUtils.save(cubeMapImage, targetFile, 0.85f);
    } catch (IOException e) {
      throw new RenderException("Could not save cube preview image", e);
    }
    log.info("Saved preview cube in: '{}'", chronograph.stop());

    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.PREVIEW_CUBIC)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
