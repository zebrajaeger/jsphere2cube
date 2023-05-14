package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.MarzipanoConfig;
import de.zebrajaeger.sphere2cube.viewer.ViewerUtils;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MarzipanoPrepareRenderStep implements RenderStep {
  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getMarzipano().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();
    MarzipanoConfig marzipanoConfig = new MarzipanoConfig(renderContext.getPanoInfo(),
        renderContext.getPanoDescription());

    marzipanoConfig.setFacebookAppId(
        renderContext.getConfig().getViewerConfig().getFacebookAppId());

    // css
    List<URL> cssFiles = renderContext.getConfig().getViewerConfig().getMarzipano().getCssFiles();
    if (cssFiles != null) {
      try {
        marzipanoConfig.getCssFiles()
            .addAll(ViewerUtils.download(cssFiles, renderContext.getOutputFolder()));
      } catch (IOException e) {
        throw new RenderException("Could not download marzipano css files", e);
      }
    }

    //js
    List<URL> jsFiles = renderContext.getConfig().getViewerConfig().getMarzipano().getJsFiles();
    if (jsFiles != null) {
      try {
        marzipanoConfig.getJsFiles()
            .addAll(ViewerUtils.download(jsFiles, renderContext.getOutputFolder()));
      } catch (IOException e) {
        throw new RenderException("Could not download marzipano js files", e);
      }
    }

    renderContext.setMarzipanoConfig(marzipanoConfig);

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_MARZIPANO_PREPARE)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
