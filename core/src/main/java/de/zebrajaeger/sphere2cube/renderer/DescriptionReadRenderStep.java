package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;


@Slf4j
public class DescriptionReadRenderStep implements RenderStep {

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
    File descriptionFile = new File(renderContext.getInputImageFile().getParentFile(),
        descriptionFileName);
    PanoDescription panoDescription;
    if (descriptionFile.exists()) {
      System.out.println("Found description file: '" + descriptionFile.getAbsolutePath() + "'");
      try {
        JsonUtils.validate(descriptionFile, "panodescription.schema.json");
        panoDescription = JsonUtils.loadJson(descriptionFile, PanoDescription.class);
      } catch (IOException e) {
        throw new RenderException(e);
      }
    } else {
      log.info("No description file found at '{}'", descriptionFile.getAbsolutePath());
      panoDescription = new PanoDescription();
      String name = renderContext.getInputImageFile().getName();
      panoDescription.setTitle(name);
      panoDescription.setDescription(name);
    }
    renderContext.setPanoDescription(panoDescription);

    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.DESCRIPTION_READ)
        .with(ValueType.SOURCE_FILE, descriptionFileName)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN,
            chronograph.getDurationForHuman()));

  }
}
