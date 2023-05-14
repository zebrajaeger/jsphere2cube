package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.StepType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class EnsureOutputFolderRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return true;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    log.info("Create target folder: '{}'", renderContext.getOutputFolder().getAbsolutePath());
    try {
      FileUtils.forceMkdir(renderContext.getOutputFolder());
    } catch (IOException e) {
      throw new RenderException("Can not create target Folder", e);
    }

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.ENSURE_OUTPUT_FOLDER)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));

  }
}
