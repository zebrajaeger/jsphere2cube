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
import org.apache.commons.io.FilenameUtils;

@Slf4j
public class DescriptionWriteMissingInSourceFolderRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return true;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    String descriptionFileName =
        FilenameUtils.getBaseName(renderContext.getInputImageFile().getName())
            + ".description.json";
    File targetFile = new File(renderContext.getInputImageFile().getParentFile(),
        descriptionFileName);

    log.info("Write description file in source folder: {}", targetFile.getAbsolutePath());
    try {
      FileUtils.forceMkdirParent(targetFile);
    } catch (IOException e) {
      throw new RenderException("Could not create folders for source description file", e);
    }

    try {
      JsonUtils.saveJson(targetFile, renderContext.getPanoDescription());
    } catch (IOException e) {
      throw new RenderException("Could not write source description file ", e);
    }
    log.info("Wrote source description file in {}", chronograph.stop());

    renderContext.addStep(PanoProcessState.Step
        .of(StepType.DESCRIPTION_WRITE_SOURCE)
        .with(ValueType.TARGET_FILE, targetFile)
        .with(ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));
  }
}
