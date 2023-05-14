package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class DescriptionWriteRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getDescriptionConfig().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File targetFile = new File(renderContext.getOutputFolder(), "pano.description.json");
    log.info("Write description file: {}", targetFile.getAbsolutePath());
    try {
      FileUtils.forceMkdirParent(targetFile);
    } catch (IOException e) {
      throw new RenderException("Could not create folders for description file", e);
    }

    try {
      JsonUtils.saveJson(targetFile, renderContext.getPanoDescription());
    } catch (IOException e) {
      throw new RenderException("Could not write description file ", e);
    }
    log.info("Wrote description file in {}", chronograph.stop());

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.DESCRIPTION_WRITE_TARGET)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
