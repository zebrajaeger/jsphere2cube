package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.Pannellum;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class PannellumHtmlRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getPannellum().isEnabled();
  }


  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File targetFile = new File(renderContext.getOutputFolder(),
        renderContext.getConfig().getViewerConfig().getPannellum().getTarget());
    log.info("Render Pannellum html: '{}'", targetFile.getAbsolutePath());

    Pannellum pannellum = new Pannellum();
    renderContext.getPannellumConfig().setTemplate(false);

    String html;
    try {
      html = pannellum.render(renderContext.getPannellumConfig());
    } catch (de.zebrajaeger.sphere2cube.viewer.RenderException e) {
      throw new RenderException("Could not render html pannellum", e);
    }

    try {
      FileUtils.write(targetFile, html, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RenderException("Could not write html pannellum file", e);
    }

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_PANNELLUM_HTML)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
