package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.image.ReadableImage;
import de.zebrajaeger.sphere2cube.progress.ConsoleProgressBar;
import de.zebrajaeger.sphere2cube.psd.PSD;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@Slf4j
public class LoadSourceRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return true;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    // load source
    log.info("Load source image: '{}'", renderContext.getInputImageFile().getAbsolutePath());
    String ext = FilenameUtils.getExtension(renderContext.getInputImageFile().getName())
        .toLowerCase();
    ReadableImage sourceImage;
    try {
      if ("psd".equals(ext) || "psb".equals(ext)) {
        sourceImage = PSD.of(renderContext.getInputImageFile(), ConsoleProgressBar.of(""));
      } else {
        sourceImage = new Img(renderContext.getInputImageFile());
      }
    } catch (IOException | InterruptedException e) {
      throw new RenderException("Can not load source image", e);
    }

    renderContext.setSourceImage(sourceImage);

    log.info("Loaded source image in '{}'", chronograph.stop());
    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.READ_SOURCE_IMAGE)
        .with(ValueType.SOURCE_FILE, renderContext.getInputImageFile())
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN, chronograph.getDurationForHuman()));


  }
}
