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
public class EquirectangularPreviewRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getPreviewsConfig().getEquirectangularPreview().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();
    log.info("Render preview equirectangular");

    File targetFile = new File(renderContext.getOutputFolder(),
        "preview_equirectangular.jpg");
    try {
      FileUtils.forceMkdirParent(targetFile);
    } catch (IOException e) {
      throw new RenderException("Could not create folder for equirectangular preview", e);
    }

    int previewEquirectangularEdge = renderContext.getConfig().getPreviewsConfig()
        .getEquirectangularPreview()
        .getEdge();
    Img scaled;
    try {
      scaled = BilinearScaler.scale(renderContext.getSource(), previewEquirectangularEdge * 2,
          previewEquirectangularEdge);
    } catch (InterruptedException e) {
      throw new RenderException("Could not scale equirectangular image", e);
    }
    log.info("Rendered preview equirectangular in '{}'", chronograph.stop());

    log.info("Save preview equirectangular: '{}'",
        targetFile.getAbsolutePath());
    chronograph = Chronograph.start();
    try {
      ImgUtils.save(scaled, targetFile, 0.85f);
    } catch (IOException e) {
      throw new RenderException("Could not save equirectangular preview image", e);
    }
    log.info("Saved preview equirectangular in: '{}'", chronograph.stop());

    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.PREVIEW_EQUIRECTANGULAR)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
