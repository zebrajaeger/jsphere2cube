package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.PannellumTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class PannellumHtmlTemplateRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getPannellum().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File targetFile = new File(renderContext.getOutputFolder(),
        renderContext.getConfig().getViewerConfig().getPannellum().getTemplateTarget());
    log.info("Render PannellumTemplate html: '{}'",
        targetFile.getAbsolutePath());

    PannellumTemplate pannellum = new PannellumTemplate();
    renderContext.getPannellumConfig().setTemplate(true);
    String html;
    try {
      html = pannellum.render(renderContext.getPannellumConfig());
    } catch (de.zebrajaeger.sphere2cube.viewer.RenderException e) {
      throw new RenderException("Could not render html pannellum template", e);
    }
    try {
      FileUtils.write(targetFile, html, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RenderException("Could not write html pannellum template", e);
    }
    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_PANNELLUM_HTML_TEMPLATE)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
