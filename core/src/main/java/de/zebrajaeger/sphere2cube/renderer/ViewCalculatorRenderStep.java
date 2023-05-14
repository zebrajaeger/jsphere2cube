package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.image.ReadableImage;
import de.zebrajaeger.sphere2cube.metadata.ViewCalculator;
import de.zebrajaeger.sphere2cube.psd.PSD;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class ViewCalculatorRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    // ensure output folder
    log.info("Create target folder: '{}'", renderContext.getOutputFolder().getAbsolutePath());
    try {
      FileUtils.forceMkdir(renderContext.getOutputFolder());
    } catch (IOException e) {
      throw new RenderException("Can not create target Folder", e);
    }
    ViewCalculator viewCalculator;
    final ReadableImage sourceImage = renderContext.getSourceImage();

    if (sourceImage instanceof PSD) {
      try {
        viewCalculator = ViewCalculator.of(renderContext.getInputImageFile(), (PSD) sourceImage);
        renderContext.setViewCalculator(viewCalculator);

        renderContext.addStep(PanoProcessState.Step
            .of(StepType.VIEW_CALCULATOR)
            .with(PanoProcessState.ValueType.VIEW_CALCULATOR, viewCalculator)
            .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
            .with(PanoProcessState.ValueType.DURATION_HUMAN,
                chronograph.getDurationForHuman()));
      } catch (Throwable t) {
        log.error("Could not calculate view", t);
      }
    }
  }
}
