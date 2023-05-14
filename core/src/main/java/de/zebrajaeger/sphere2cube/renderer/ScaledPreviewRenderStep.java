package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.ImgUtils;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class ScaledPreviewRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getPreviewsConfig().getScaledPreview().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();
    log.info("Render preview scaled");

    File targetFile = new File(renderContext.getOutputFolder(),
        "preview_scaled.jpg");
    try {
      FileUtils.forceMkdirParent(targetFile);
    } catch (IOException e) {
      throw new RenderException("Could not create directory for scaled preview", e);
    }

    int previewScaledOriginalEdge = renderContext.getConfig().getPreviewsConfig().getScaledPreview()
        .getEdge();
    float factor;
    if (renderContext.getSourceImage().getWidth() > renderContext.getSourceImage().getHeight()) {
      factor =
          (float) renderContext.getSourceImage().getWidth() / (float) previewScaledOriginalEdge;
    } else {
      factor =
          (float) renderContext.getSourceImage().getHeight() / (float) previewScaledOriginalEdge;
    }

    Img scaled;
    try {
      scaled = BilinearScaler.scale(renderContext.getSourceImage(),
          (int) (renderContext.getSource().getWidth() / factor),
          (int) (renderContext.getSource().getHeight() / factor));
    } catch (InterruptedException e) {
      throw new RenderException("Could not scale image", e);
    }
    log.info("Rendered preview scaled in '{}'", chronograph.stop());

    log.info("Save preview scaled: '{}'", targetFile.getAbsolutePath());
    chronograph = Chronograph.start();
    int previewScaledMaxSize = renderContext.getConfig().getPreviewsConfig().getScaledPreview()
        .getMaxSize();
    try {
      ImgUtils.save(scaled, targetFile, previewScaledMaxSize);
    } catch (IOException e) {
      throw new RenderException("Could not save image", e);
    }
    log.info("Saved preview scaled in: '{}'", chronograph.stop());

    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.PREVIEW_SCALED)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
