package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.Defaults;
import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.runconfig.LastRun;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.HashUtils;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class LastRunFileRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    return true;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    Chronograph chronograph = Chronograph.start();

    if (renderContext.getOutputFolder().exists()) {
      LastRun lastRun = new LastRun();
      lastRun.setPanoProcessState(renderContext.getResult());
      if (renderContext.getSrcConfigFile() != null) {
        try {
          lastRun.setConfigHash(HashUtils.hashFile(renderContext.getSrcConfigFile()));
        } catch (IOException e) {
          throw new RenderException("Could not create src config file hash", e);
        }
      } else {
        File configFile = new File(renderContext.getOutputFolder(), Defaults.CONFIG_FILE_NAME);
        if (configFile.exists()) {
          try {
            lastRun.setConfigHash(HashUtils.hashFile(configFile));
          } catch (IOException e) {
            throw new RenderException("Could not create config file hash", e);
          }
        }
      }

      File targetFile = new File(new File(renderContext.getOutputFolder(),
          Defaults.LAST_RUN_CONFIG_FOLDER_NAME),
          Defaults.LAST_RUN_CONFIG_FILE_NAME);
      try {
        FileUtils.forceMkdirParent(targetFile);
      } catch (IOException e) {
        throw new RenderException("Could not create folder for last run file", e);
      }

      try {
        JsonUtils.saveJson(targetFile, lastRun);
      } catch (IOException e) {
        throw new RenderException("Could not save last run file", e);
      }

      renderContext.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.LAST_RUN_FILE)
          .with(ValueType.TARGET_FILE, targetFile)
          .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              chronograph.getDurationForHuman()));
    }
  }
}
