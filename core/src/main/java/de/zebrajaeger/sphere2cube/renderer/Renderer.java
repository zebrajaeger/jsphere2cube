package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.image.Pixel;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Renderer {

  public PanoProcessState renderPano(File root, Config config, File srcConfigFile,
      Pixel backgroundColor) throws RenderException {
    Chronograph appChronograph = Chronograph.start();

    File inputImageFile = toAbsoluteFile(root, config.getInputConfig().getInputImageFile());
    File outputFolder = toAbsoluteFile(root, config.getOutputFolder());
    final RenderContext renderContext = new RenderContext(
        inputImageFile,
        outputFolder,
        srcConfigFile, config,
        backgroundColor);

    List<RenderStep> steps = new ArrayList<>();
    steps.add(new EnsureOutputFolderRenderStep());
    steps.add(new DescriptionReadRenderStep());
    steps.add(new LoadSourceRenderStep());
    steps.add(new ViewCalculatorRenderStep());
    steps.add(new SourceRenderStep());
    steps.add(new DescriptionWriteRenderStep());
    steps.add(new DescriptionWriteMissingInSourceFolderRenderStep());
    steps.add(new CubePreviewRenderStep());
    steps.add(new EquirectangularPreviewRenderStep());
    steps.add(new ScaledPreviewRenderStep());
    steps.add(new CubeMapFacesRenderStep());
    steps.add(new PannellumPrepareRenderStep());
    steps.add(new PannellumHtmlRenderStep());
    steps.add(new PannellumHtmlTemplateRenderStep());
    steps.add(new MarzipanoPrepareRenderStep());
    steps.add(new MarzipanoHtmlRenderStep());
    steps.add(new MarzipanoHtmlTemplateRenderStep());
    steps.add(new ZipRenderStep());
    steps.add(new LastRunFileRenderStep());

    for (RenderStep step : steps) {
      if (step.isEnabled(renderContext)) {
        step.render(renderContext);
      }
    }

    log.info("Completed in {}", appChronograph.stop());
    renderContext.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.FINISHED)
        .with(PanoProcessState.ValueType.DURATION_MS, appChronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN, appChronograph.getDurationForHuman()));
    return renderContext.getResult();
  }

  private File toAbsoluteFile(File root, String path) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(root, path);
    }
    try {
      file = file.getCanonicalFile();
    } catch (IOException e) {
      log.error("getCanonicalFile of '{}'/'{}", root.getAbsolutePath(), path, e);
    }
    return file;
  }
}
