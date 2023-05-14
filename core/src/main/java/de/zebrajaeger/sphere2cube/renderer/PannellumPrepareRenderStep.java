package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.PannellumConfig;
import de.zebrajaeger.sphere2cube.viewer.ViewerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PannellumPrepareRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getPannellum().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    int tileEdge = renderContext.getConfig().getCubeMapConfig().getTiles().getTileEdge();

    PannellumConfig pannellumConfig = new PannellumConfig(
        renderContext.getPanoInfo().getMaxLevelIndex() + 1,
        renderContext.getPanoInfo().getSourceFaceEdge(),
        tileEdge,
        renderContext.getPanoDescription());

    pannellumConfig.setFacebookAppId(
        renderContext.getConfig().getViewerConfig().getFacebookAppId());

    // css
    List<URL> cssFiles = renderContext.getConfig().getViewerConfig().getPannellum().getCssFiles();
    if (cssFiles != null) {
      try {
        pannellumConfig.getCssFiles()
            .addAll(ViewerUtils.download(cssFiles, renderContext.getOutputFolder()));
      } catch (IOException e) {
        throw new RenderException("Could not add css file to pannellum viewer", e);
      }
    }

    //js
    List<URL> jsFiles = renderContext.getConfig().getViewerConfig().getPannellum().getJsFiles();
    if (jsFiles != null) {
      try {
        pannellumConfig.getJsFiles()
            .addAll(ViewerUtils.download(jsFiles, renderContext.getOutputFolder()));
      } catch (IOException e) {
        throw new RenderException("Could not add js files to pannellum viewer", e);
      }
    }

    if (renderContext.getViewCalculator() != null) {
      renderContext.getViewCalculator().createPanoView().ifPresent(pv -> {
        pannellumConfig.setXMin(-pv.getFovX() / 2);
        pannellumConfig.setXMax(pv.getFovX() / 2);

        pannellumConfig.setYMin(pv.getFovY1Inv());
        pannellumConfig.setYMax(pv.getFovY2Inv());
      });
    }

    renderContext.setPannellumConfig(pannellumConfig);

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_PANNELLUM_PREPARE)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
