package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.Marzipano;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class MarzipanoHtmlRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getMarzipano().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File targetFile = new File(renderContext.getOutputFolder(),
        renderContext.getConfig().getViewerConfig().getMarzipano().getTarget());
    log.info("Render Marzipano html: '{}'", targetFile.getAbsolutePath());

    Marzipano marzipano = new Marzipano();
    renderContext.getMarzipanoConfig().setTemplate(false);
    String html;
    try {
      html = marzipano.render(renderContext.getMarzipanoConfig());
    } catch (de.zebrajaeger.sphere2cube.viewer.RenderException e) {
      throw new RenderException("Could not render marzipano html", e);
    }

    try {
      FileUtils.write(targetFile, html, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RenderException("Could not write marzipano html file", e);
    }

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_MARZIPANO_HTML)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));

  }
}
