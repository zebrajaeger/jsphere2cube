package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.zip.Zipper;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZipRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return renderContext.getConfig().getArchiveConfig().isEnabled();
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    File archiveFile = new File(renderContext.getOutputFolder(),
        renderContext.getConfig().getArchiveConfig().getTarget());

    try {
      Zipper.compress(renderContext.getOutputFolder(), archiveFile);
    } catch (IOException | ExecutionException | InterruptedException e) {
      throw new RenderException("Could not compress files", e);
    }

    log.info("Archived to '{}' in {}", archiveFile.getAbsolutePath(), chronograph.stop());
    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.ARCHIVE)
        .with(ValueType.TARGET_FILE, archiveFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN, chronograph.getDurationForHuman()));
  }
}
