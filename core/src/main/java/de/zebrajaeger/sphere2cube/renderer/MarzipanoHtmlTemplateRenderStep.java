package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.viewer.MarzipanoTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class MarzipanoHtmlTemplateRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getViewerConfig().getMarzipano().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File targetFile = new File(renderContext.getOutputFolder(),
        renderContext.getConfig().getViewerConfig().getMarzipano().getTemplateTarget());
    log.info("Render MarzipanoTemplate html: '{}'",
        targetFile.getAbsolutePath());

    MarzipanoTemplate marzipanoTemplate = new MarzipanoTemplate();
    renderContext.getMarzipanoConfig().setTemplate(true);
    String html;
    try {
      html = marzipanoTemplate.render(renderContext.getMarzipanoConfig());
    } catch (de.zebrajaeger.sphere2cube.viewer.RenderException e) {
      throw new RenderException("Could not render marzipano html template", e);
    }
    try {
      FileUtils.write(targetFile, html, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RenderException("Could not write marzipano html template", e);
    }

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.VIEWER_MARZIPANO_HTML_TEMPLATE)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
