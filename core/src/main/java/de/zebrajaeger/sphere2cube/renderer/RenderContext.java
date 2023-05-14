package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.Step;
import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.image.Pixel;
import de.zebrajaeger.sphere2cube.image.ReadableImage;
import de.zebrajaeger.sphere2cube.metadata.ViewCalculator;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;
import de.zebrajaeger.sphere2cube.viewer.MarzipanoConfig;
import de.zebrajaeger.sphere2cube.viewer.PannellumConfig;
import java.io.File;
import lombok.Data;

@Data
public class RenderContext {

  private final File inputImageFile;
  private final File outputFolder;
  private final File srcConfigFile;
  private final Config config;
  private final Pixel backgroundColor;
  private final PanoProcessState result;

  public RenderContext(File inputImageFile, File outputFolder, File srcConfigFile, Config config,
      Pixel backgroundColor) {
    this.inputImageFile = inputImageFile;
    this.outputFolder = outputFolder;
    this.srcConfigFile = srcConfigFile;
    this.config = config;
    this.backgroundColor = backgroundColor;
    result = new PanoProcessState(outputFolder);
  }

  private PanoDescription panoDescription;
  private PanoInfo panoInfo;
  private EquirectangularImage source;
  private ReadableImage sourceImage;
  private ViewCalculator viewCalculator;
  private PannellumConfig pannellumConfig;
  private MarzipanoConfig marzipanoConfig;

  public void addStep(Step step) {
    result.addStep(step);
  }
}
