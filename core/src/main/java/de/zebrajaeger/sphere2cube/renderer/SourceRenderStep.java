package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.metadata.ViewCalculator;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return true;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    double inputImageHorizontalAngel = 360;
    double inputImageVerticalOffset = 0;

    // take angle from metadata if available
    final ViewCalculator viewCalculator = renderContext.getViewCalculator();
    if (viewCalculator != null) {
      log.info("ViewCalculator: {}", viewCalculator);
      inputImageHorizontalAngel = viewCalculator.getFovX();
      inputImageVerticalOffset = viewCalculator.getFovYOffset();
    }

    // overwrite manual if present
    if (renderContext.getConfig().getInputConfig().getInputImageHorizontalAngel() != null
        && renderContext.getConfig().getInputConfig().getInputImageHorizontalAngel() > 0) {
      inputImageHorizontalAngel = renderContext.getConfig().getInputConfig()
          .getInputImageHorizontalAngel();
      inputImageVerticalOffset = renderContext.getConfig().getInputConfig()
          .getInputImageVerticalOffset();
    }

    // create equirectangular source image
    EquirectangularImage source = EquirectangularImage.of(
        renderContext.getSourceImage(),
        inputImageHorizontalAngel,
        inputImageVerticalOffset,
        renderContext.getBackgroundColor());

    int tileEdge = renderContext.getConfig().getCubeMapConfig().getTiles().getTileEdge();
    renderContext.setPanoInfo(PanoUtils.calcPanoInfo(source, tileEdge));

    renderContext.setSource(source);

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.SOURCE)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
