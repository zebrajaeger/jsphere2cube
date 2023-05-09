package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.image.CubeMapImage;
import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.image.Pixel;
import de.zebrajaeger.sphere2cube.image.ReadableImage;
import de.zebrajaeger.sphere2cube.metadata.ViewCalculator;
import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import de.zebrajaeger.sphere2cube.names.CubeFaceNameGenerator;
import de.zebrajaeger.sphere2cube.names.TileNameGenerator;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoLevel;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;
import de.zebrajaeger.sphere2cube.progress.ConsoleProgressBar;
import de.zebrajaeger.sphere2cube.progress.Progress;
import de.zebrajaeger.sphere2cube.psd.PSD;
import de.zebrajaeger.sphere2cube.runconfig.LastRun;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.scaler.DownHalfScaler;
import de.zebrajaeger.sphere2cube.tiles.TileSaveJob;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.HashUtils;
import de.zebrajaeger.sphere2cube.util.ImgUtils;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import de.zebrajaeger.sphere2cube.viewer.Marzipano;
import de.zebrajaeger.sphere2cube.viewer.MarzipanoConfig;
import de.zebrajaeger.sphere2cube.viewer.MarzipanoTemplate;
import de.zebrajaeger.sphere2cube.viewer.Pannellum;
import de.zebrajaeger.sphere2cube.viewer.PannellumConfig;
import de.zebrajaeger.sphere2cube.viewer.PannellumTemplate;
import de.zebrajaeger.sphere2cube.viewer.ViewerUtils;
import de.zebrajaeger.sphere2cube.zip.Zipper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

@Slf4j
public class Sphere2CubeRenderer {

  private File toAbsoluteFile(File root, String path) {
    File inputImageFile = new File(path);
    if (!inputImageFile.isAbsolute()) {
      inputImageFile = new File(root, path);
    }
    try {
      inputImageFile = inputImageFile.getCanonicalFile();
    } catch (IOException e) {
      log.error("getCanonicalFile of '{}'/'{}", root.getAbsolutePath(), path, e);
    }
    return inputImageFile;
  }

  public PanoProcessState renderPano(File root, Config config, File srcConfigFile,
      Pixel backgroundColor)
      throws IOException, InterruptedException, ExecutionException {
    Chronograph appChronograph = Chronograph.start();
    File outputFolder = toAbsoluteFile(root, config.getOutputFolder());

    PanoProcessState result = new PanoProcessState(outputFolder);

    // +===============================================================
    // | Options
    // +===============================================================

    boolean debug = config.isDebug();

    // Source
    File inputImageFile = toAbsoluteFile(root, config.getInputConfig().getInputImageFile());

    // Description
    boolean descriptionEnabled = config.getDescriptionConfig().isEnabled();
    File descriptionTarget = new File(outputFolder, "pano.description.json");

    // Preview - CubeMap
    boolean previewCubeEnabled = config.getPreviewsConfig().getCubeMapPreview().isEnabled();
    int previewCubeEdge = config.getPreviewsConfig().getCubeMapPreview().getEdge();
    File previewCubeTarget = new File(outputFolder, "preview_cube.jpg");

    // Preview Equirectangular
    boolean previewEquirectangularEnabled = config.getPreviewsConfig().getEquirectangularPreview()
        .isEnabled();
    int previewEquirectangularEdge = config.getPreviewsConfig().getEquirectangularPreview()
        .getEdge();
    File previewEquirectangularTarget = new File(outputFolder, "preview_equirectangular.jpg");

    // Preview Scaled
    boolean previewScaledOriginalEnabled = config.getPreviewsConfig().getScaledPreview()
        .isEnabled();
    int previewScaledOriginalEdge = config.getPreviewsConfig().getScaledPreview().getEdge();
    int previewScaledMaxSize = config.getPreviewsConfig().getScaledPreview().getMaxSize();
    File previewScaledOriginalTarget = new File(outputFolder, "preview_scaled.jpg");

    // Cube faces
    boolean cubeMapFacesEnabled = config.getCubeMapConfig().getFaces().isEnabled();
    String cubeFaceTarget = config.getCubeMapConfig().getFaces().getTarget();

    boolean cubeMapTilesEnabled = config.getCubeMapConfig().getTiles().isEnabled();
    String cubeFaceTilesTarget = config.getCubeMapConfig().getTiles().getTarget();
    int tileEdge = config.getCubeMapConfig().getTiles().getTileEdge();

    // Archive
    boolean archiveEnabled = config.getArchiveConfig().isEnabled();
    File archiveFile = new File(outputFolder, config.getArchiveConfig().getTarget());

    // +===============================================================
    // | Read Description
    // +===============================================================
    String descriptionFileName =
        FilenameUtils.getBaseName(inputImageFile.getName()) + ".description.json";
    File descriptionFile = new File(inputImageFile.getParentFile(), descriptionFileName);
    PanoDescription panoDescription;
    if (descriptionFile.exists()) {
      System.out.println("Found description file: '" + descriptionFile.getAbsolutePath() + "'");
      JsonUtils.validate(descriptionFile, "panodescription.schema.json");
      panoDescription = JsonUtils.loadJson(descriptionFile, PanoDescription.class);
    } else {
      System.out.println(
          "No description file found at '" + descriptionFile.getAbsolutePath() + "'");
      panoDescription = new PanoDescription();
      String name = inputImageFile.getName();
      panoDescription.setTitle(name);
      panoDescription.setDescription(name);
    }

    // +===============================================================
    // | Init and load source
    // +===============================================================

    // ensure output folder
    log.info("Create target folder: '{}'", outputFolder.getAbsolutePath());
    FileUtils.forceMkdir(outputFolder);

    // load source
    log.info("Load source image: '{}'", inputImageFile.getAbsolutePath());
    String ext = FilenameUtils.getExtension(inputImageFile.getName()).toLowerCase();
    ReadableImage sourceImage;
    Chronograph chronograph = Chronograph.start();
    if ("psd".equals(ext) || "psb".equals(ext)) {
      sourceImage = PSD.of(inputImageFile, ConsoleProgressBar.of(""));
    } else {
      sourceImage = new Img(inputImageFile);
    }
    log.info("Loaded source image in '{}'", chronograph.stop());
    result.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.READ_SOURCE_IMAGE)
        .with(PanoProcessState.ValueType.FILE, inputImageFile)
        .with(PanoProcessState.ValueType.DURATION_MS, chronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN, chronograph.getDurationForHuman()));

    // View for viewer
    Optional<ViewCalculator> viewCalculator = Optional.empty();
    if (config.getViewerConfig().isEnabled()) {
      if (sourceImage instanceof PSD) {
        try {
          viewCalculator = Optional.of(ViewCalculator.of(inputImageFile, (PSD) sourceImage));
        } catch (Throwable t) {
          log.error("Could not calculate view", t);
        }
      }
    }
    result.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.CALCULATE_VIEW)
        .with(PanoProcessState.ValueType.VIEW_CALCULATOR, viewCalculator.orElse(null)));

    double inputImageHorizontalAngel = 360;
    double inputImageVerticalOffset = 0;

    // take angel from metadata if available
    if (viewCalculator.isPresent()) {
      log.info(viewCalculator.get().toString());
      inputImageHorizontalAngel = viewCalculator.get().getFovX();
      inputImageVerticalOffset = viewCalculator.get().getFovYOffset();
    }

    // overwrite manual if present
    if (config.getInputConfig().getInputImageHorizontalAngel() != null
        && config.getInputConfig().getInputImageHorizontalAngel() > 0) {
      inputImageHorizontalAngel = config.getInputConfig().getInputImageHorizontalAngel();
      inputImageVerticalOffset = config.getInputConfig().getInputImageVerticalOffset();
    }

    // create equirectangular source image
    EquirectangularImage source = EquirectangularImage.of(sourceImage, inputImageHorizontalAngel,
        inputImageVerticalOffset, backgroundColor);
    PanoInfo panoInfo = PanoUtils.calcPanoInfo(source, tileEdge);

    // +===============================================================
    // | Description
    // +===============================================================
    if (descriptionEnabled) {
      log.info("Write description file: {}", descriptionTarget.getAbsolutePath());
      Chronograph descriptionChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(descriptionTarget);
      JsonUtils.saveJson(descriptionTarget, panoDescription);
      log.info("Wrote description file in {}", descriptionChronograph.stop());

      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.DESCRIPTION)
          .with(PanoProcessState.ValueType.FILE, descriptionTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, descriptionChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              descriptionChronograph.getDurationForHuman()));
    }

    // +===============================================================
    // | Preview(s)
    // +===============================================================

    // generate cube preview
    if (previewCubeEnabled) {
      log.info("Render preview cubemap");
      Chronograph previewChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(previewCubeTarget);
      CubeMapImage cubeMapImage = new CubeMapImage(previewCubeEdge);
      for (Face face : Face.values()) {
        FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face, Progress.DUMMY);
      }
      log.info("Rendered preview cubemap in {}", previewChronograph.stop());

      log.info("Save preview cube: '{}'", previewCubeTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(cubeMapImage, previewCubeTarget, 0.85f);
      log.info("Saved preview cube in: '{}'", previewChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.PREVIEW_CUBIC)
          .with(PanoProcessState.ValueType.FILE, previewCubeTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, previewChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              previewChronograph.getDurationForHuman()));
    }

    // generate Equirectangular preview
    if (previewEquirectangularEnabled) {
      log.info("Render preview equirectangular");
      Chronograph previewChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(previewEquirectangularTarget);
      Img scaled = BilinearScaler.scale(source, previewEquirectangularEdge * 2,
          previewEquirectangularEdge);
      log.info("Rendered preview equirectangular in '{}'", previewChronograph.stop());

      log.info("Save preview equirectangular: '{}'",
          previewEquirectangularTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(scaled, previewEquirectangularTarget, 0.85f);
      log.info("Saved preview equirectangular in: '{}'", previewChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.PREVIEW_EQUIRECTANGULAR)
          .with(PanoProcessState.ValueType.FILE, previewEquirectangularTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, previewChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              previewChronograph.getDurationForHuman()));
    }

    // generate scaled original preview
    if (previewScaledOriginalEnabled) {
      log.info("Render preview scaled");
      Chronograph previewChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(previewScaledOriginalTarget);

      float factor;
      if (sourceImage.getWidth() > sourceImage.getHeight()) {
        factor = (float) sourceImage.getWidth() / (float) previewScaledOriginalEdge;
      } else {
        factor = (float) sourceImage.getHeight() / (float) previewScaledOriginalEdge;
      }
      Img scaled = BilinearScaler.scale(sourceImage, (int) (source.getWidth() / factor),
          (int) (source.getHeight() / factor));
      log.info("Rendered preview scaled in '{}'", previewChronograph.stop());

      log.info("Save preview scaled: '{}'", previewScaledOriginalTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(scaled, previewScaledOriginalTarget, previewScaledMaxSize);
      log.info("Saved preview scaled in: '{}'", previewChronograph.stop());

      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.PREVIEW_SCALED)
          .with(PanoProcessState.ValueType.FILE, previewScaledOriginalTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, previewChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              previewChronograph.getDurationForHuman()));
    }

    // cube map faces
    if (cubeMapFacesEnabled || cubeMapTilesEnabled) {
      CubeFaceNameGenerator cubeFaceNameGenerator = new CubeFaceNameGenerator(cubeFaceTarget);
      TileNameGenerator tileNameGenerator = new TileNameGenerator(cubeFaceTilesTarget);

      log.info(panoInfo.toString());
      int faceEdge = panoInfo.getSourceFaceEdge();

      Img cubeFace = Img.rectangular(faceEdge);
      int faceCount = 0;
      for (Face face : Face.values()) {
        faceCount++;
        Chronograph faceChronograph = Chronograph.start();

        // render face
        log.info("Render face: '{}'({}/6) - {}x{}", face, faceCount, faceEdge, faceEdge);
        Chronograph faceRenderChronograph = Chronograph.start();
        FaceRenderExecutor.renderFace(source, cubeFace, face,
            ConsoleProgressBar.of(String.format("Render %s", face)));
        log.info("Render face in '{}'", faceRenderChronograph.stop());
        result.addStep(PanoProcessState.Step
            .of(PanoProcessState.StepType.FACE)
            .with(PanoProcessState.ValueType.FACE, face)
            .with(PanoProcessState.ValueType.DURATION_MS, faceRenderChronograph.getDurationMs())
            .with(PanoProcessState.ValueType.DURATION_HUMAN,
                faceRenderChronograph.getDurationForHuman()));
        if (debug) {
          ImgUtils.drawBorder(cubeFace, face.getColor());
        }

        Img scaledCubeFace = cubeFace;
        for (int levelIndex = panoInfo.getMaxLevelIndex(); levelIndex >= 0; --levelIndex) {
          PanoLevel level = panoInfo.getLevel(levelIndex);

          // save face image
          if (cubeMapFacesEnabled) {
            File faceFile = new File(outputFolder,
                cubeFaceNameGenerator.generate(panoInfo, levelIndex, face));
            log.info("Save cube face: '{}'({}/6) -> {}", face, faceCount,
                faceFile.getAbsolutePath());
            Chronograph cubeFaceSaveChronograph = Chronograph.start();
            FileUtils.forceMkdirParent(faceFile);
            ImgUtils.save(scaledCubeFace, faceFile, null);
            log.info("Save cube face in '{}'", cubeFaceSaveChronograph.stop());
            result.addStep(PanoProcessState.Step
                .of(PanoProcessState.StepType.SAVE_FACE)
                .with(PanoProcessState.ValueType.FILE, faceFile)
                .with(PanoProcessState.ValueType.LEVEL_INDEX, levelIndex)
                .with(PanoProcessState.ValueType.FACE, face)
                .with(PanoProcessState.ValueType.DURATION_MS,
                    cubeFaceSaveChronograph.getDurationMs())
                .with(PanoProcessState.ValueType.DURATION_HUMAN,
                    cubeFaceSaveChronograph.getDurationForHuman()));
          }

          // render tiles for face and level
          int lineTileCount = level.getTileCount();
          int tileCount = lineTileCount * lineTileCount;

          log.info("Save tiles of '{}'({}/6): {}", face, faceCount, tileCount);
          Chronograph tileSaveChronograph = Chronograph.start();
          MaxJobQueueExecutor tileExecutor = MaxJobQueueExecutor.withMaxQueueSize();
          ConsoleProgressBar tileProgressBar = ConsoleProgressBar.of(
              String.format("Tiles of %s", face));
          tileProgressBar.start(tileCount);
          for (int yIndex = 0; yIndex < lineTileCount; ++yIndex) {
            for (int xIndex = 0; xIndex < lineTileCount; ++xIndex) {
              String name = tileNameGenerator.generate(panoInfo, levelIndex, face, xIndex, yIndex);

              tileExecutor.addJob(new TileSaveJob(
                  scaledCubeFace,
                  new File(outputFolder, name),
                  tileEdge,
                  xIndex * tileEdge,
                  yIndex * tileEdge,
                  debug));
              tileProgressBar.update((long) yIndex * lineTileCount + xIndex);
            }
          }
          tileExecutor.shutdown();
          tileProgressBar.finish();
          log.info("Tiles saved in {}", tileSaveChronograph.stop());
          result.addStep(PanoProcessState.Step
              .of(PanoProcessState.StepType.SAVE_TILES)
              .with(PanoProcessState.ValueType.LEVEL_INDEX, levelIndex)
              .with(PanoProcessState.ValueType.FACE, face)
              .with(PanoProcessState.ValueType.DURATION_MS, tileSaveChronograph.getDurationMs())
              .with(PanoProcessState.ValueType.DURATION_HUMAN,
                  tileSaveChronograph.getDurationForHuman()));

          // downscale cube face image
          if (levelIndex > 0) {
            Chronograph downscaleChronograph = Chronograph.start();
            int newEdge2 = level.getFaceEdge() / 2;
            log.info("Downscale face '{}'({}/6) to 1/2 = {},{}", face, faceCount, newEdge2,
                newEdge2);
            ConsoleProgressBar downHalfScaleProgress = ConsoleProgressBar.of(
                String.format("Downscale of %s", face));
            scaledCubeFace = DownHalfScaler.scale(scaledCubeFace, downHalfScaleProgress);
            log.info("Downscaled face in {}", downscaleChronograph.stop());
            result.addStep(PanoProcessState.Step
                .of(PanoProcessState.StepType.SCALE_LEVEL)
                .with(PanoProcessState.ValueType.LEVEL_INDEX, levelIndex)
                .with(PanoProcessState.ValueType.DURATION_MS, downscaleChronograph.getDurationMs())
                .with(PanoProcessState.ValueType.DURATION_HUMAN,
                    downscaleChronograph.getDurationForHuman()));
          }
        }

        log.info("Face '{}' completed in {}", face, faceChronograph.stop());
      }
    }

    // Viewer - Pannellum
    if (config.getViewerConfig().getPannellum().isEnabled()) {
      PannellumConfig pannellumConfig = new PannellumConfig(
          panoInfo.getMaxLevelIndex() + 1,
          panoInfo.getSourceFaceEdge(),
          tileEdge,
          panoDescription);

      pannellumConfig.setFacebookAppId(config.getViewerConfig().getFacebookAppId());

      // css
      List<URL> cssFiles = config.getViewerConfig().getPannellum().getCssFiles();
      if (cssFiles != null) {
        pannellumConfig.getCssFiles().addAll(ViewerUtils.download(cssFiles, outputFolder));
      }

      //js
      List<URL> jsFiles = config.getViewerConfig().getPannellum().getJsFiles();
      if (jsFiles != null) {
        pannellumConfig.getJsFiles().addAll(ViewerUtils.download(jsFiles, outputFolder));
      }

      viewCalculator.ifPresent(vc -> {
        vc.createPanoView().ifPresent(pv -> {
          pannellumConfig.setXMin(-pv.getFovX() / 2);
          pannellumConfig.setXMax(pv.getFovX() / 2);

          pannellumConfig.setYMin(pv.getFovY1Inv());
          pannellumConfig.setYMax(pv.getFovY2Inv());
        });
      });

      {
        // pannellum target File
        File viewerPannellumFile = new File(outputFolder,
            config.getViewerConfig().getPannellum().getTarget());
        log.info("Render Pannellum html: '{}'", viewerPannellumFile.getAbsolutePath());

        Pannellum pannellum = new Pannellum();
        pannellumConfig.setTemplate(false);
        String html = pannellum.render(pannellumConfig);
        FileUtils.write(viewerPannellumFile, html, StandardCharsets.UTF_8);
        result.addStep(PanoProcessState.Step
            .of(PanoProcessState.StepType.VIEWER_PANNELLUM)
            .with(PanoProcessState.ValueType.FILE, viewerPannellumFile));
      }
      {
        // panellum target template  File
        File viewerPannellumTemplateFile = new File(outputFolder,
            config.getViewerConfig().getPannellum().getTemplateTarget());
        log.info("Render PannellumTemplate html: '{}'",
            viewerPannellumTemplateFile.getAbsolutePath());

        PannellumTemplate pannellum = new PannellumTemplate();
        pannellumConfig.setTemplate(true);
        String html = pannellum.render(pannellumConfig);
        FileUtils.write(viewerPannellumTemplateFile, html, StandardCharsets.UTF_8);
        result.addStep(PanoProcessState.Step
            .of(PanoProcessState.StepType.VIEWER_PANNELLUM)
            .with(PanoProcessState.ValueType.FILE, viewerPannellumTemplateFile));
      }
    }

    // Viewer - Marzipano
    if (config.getViewerConfig().getMarzipano().isEnabled()) {
      MarzipanoConfig marzipanoConfig = new MarzipanoConfig(panoInfo, panoDescription);

      marzipanoConfig.setFacebookAppId(config.getViewerConfig().getFacebookAppId());

      // css
      List<URL> cssFiles = config.getViewerConfig().getMarzipano().getCssFiles();
      if (cssFiles != null) {
        marzipanoConfig.getCssFiles().addAll(ViewerUtils.download(cssFiles, outputFolder));
      }

      //js
      List<URL> jsFiles = config.getViewerConfig().getMarzipano().getJsFiles();
      if (jsFiles != null) {
        marzipanoConfig.getJsFiles().addAll(ViewerUtils.download(jsFiles, outputFolder));
      }

      {
        // target file
        File viewerMarzipanoFile = new File(outputFolder,
            config.getViewerConfig().getMarzipano().getTarget());
        log.info("Render Marzipano html: '{}'", viewerMarzipanoFile.getAbsolutePath());

        Marzipano marzipano = new Marzipano();
        marzipanoConfig.setTemplate(false);
        String html = marzipano.render(marzipanoConfig);
        FileUtils.write(viewerMarzipanoFile, html, StandardCharsets.UTF_8);
        result.addStep(PanoProcessState.Step
            .of(PanoProcessState.StepType.VIEWER_MARZIPANO)
            .with(PanoProcessState.ValueType.FILE, viewerMarzipanoFile));
      }
      {
        // target template file
        File viewerMarzipanoTemplateFile = new File(outputFolder,
            config.getViewerConfig().getMarzipano().getTemplateTarget());
        log.info("Render MarzipanoTemplate html: '{}'",
            viewerMarzipanoTemplateFile.getAbsolutePath());

        MarzipanoTemplate marzipanoTemplate = new MarzipanoTemplate();
        marzipanoConfig.setTemplate(true);
        String html = marzipanoTemplate.render(marzipanoConfig);
        FileUtils.write(viewerMarzipanoTemplateFile, html, StandardCharsets.UTF_8);
        result.addStep(PanoProcessState.Step
            .of(PanoProcessState.StepType.VIEWER_MARZIPANO)
            .with(PanoProcessState.ValueType.FILE, viewerMarzipanoTemplateFile));
      }
    }

    // Archive
    if (archiveEnabled) {
      Chronograph zipChronograph = Chronograph.start();
      Zipper.compress(outputFolder, archiveFile);
      log.info("Archived to '{}' in {}", archiveFile.getAbsolutePath(), zipChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.ARCHIVE)
          .with(PanoProcessState.ValueType.FILE, archiveFile)
          .with(PanoProcessState.ValueType.DURATION_MS, zipChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN, zipChronograph.getDurationForHuman()));
    }

    log.info("Completed in {}", appChronograph.stop());
    result.addStep(PanoProcessState.Step
        .of(PanoProcessState.StepType.FINISHED)
        .with(PanoProcessState.ValueType.DURATION_MS, appChronograph.getDurationMs())
        .with(PanoProcessState.ValueType.DURATION_HUMAN, appChronograph.getDurationForHuman()));

    if (outputFolder.exists()) {
      LastRun lastRun = new LastRun();
      lastRun.setPanoProcessState(result);
      if (srcConfigFile != null) {
        lastRun.setConfigHash(HashUtils.hashFile(srcConfigFile));
      } else {
        File configFile = new File(outputFolder, Defaults.CONFIG_FILE_NAME);
        if (configFile.exists()) {
          lastRun.setConfigHash(HashUtils.hashFile(configFile));
        }
      }
      File lastRunFile = new File(new File(outputFolder, Defaults.LAST_RUN_CONFIG_FOLDER_NAME),
          Defaults.LAST_RUN_CONFIG_FILE_NAME);
      FileUtils.forceMkdirParent(lastRunFile);
      JsonUtils.saveJson(lastRunFile, lastRun);
    }

    return result;
  }
}
