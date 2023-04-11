package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
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
import de.zebrajaeger.sphere2cube.runconfig.PanoDirectory;
import de.zebrajaeger.sphere2cube.runconfig.PanoSearcher;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.scaler.DownHalfScaler;
import de.zebrajaeger.sphere2cube.tiles.TileSaveJob;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  public static final Pixel DEFAULT_BACKGROUND_COLOR = new Pixel(0, 0, 0);
  private static final Logger LOG = LoggerFactory.getLogger(App.class);
  public static final String DEFAULT_CONFIG_FILE_NAME = "sphere2cube.json";
  public static final String DEFAULT_LAST_RUN_CONFIG_FOLDER_NAME = ".sphere2cube";
  public static final String DEFAULT_LAST_RUN_CONFIG_FILE_NAME = "last-run.json";

  public static void main(String[] args)
      throws IOException, InterruptedException, ParseException, ExecutionException {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    Config config;
    File srcConfigFile = null;

    if (args.length == 0) {
      File src = new File(DEFAULT_CONFIG_FILE_NAME);
      if (!src.exists()) {
        Config.help();
        System.exit(-1);
        return;
      } else {
        srcConfigFile = src;
        config = Config.of(src);
      }
    } else if (args.length == 1) {
      File src = new File(args[0]);
      if (!src.exists()) {
        Config.help();
        System.exit(-1);
        return;
      } else {
        if (src.isFile()) {
          srcConfigFile = src;
          config = Config.of(src);
        } else if (src.isDirectory()) {

          renderAll(src);
          return;
        } else {
          return;
        }
      }
    } else {
      config = Config.of(args);
    }

    if (config.getSaveConfig().isSaveConfig()) {
      File t = new File(config.getSaveConfig().getSaveConfigTarget());
      FileUtils.forceMkdirParent(t);
      JsonUtils.saveJson(t, config);
    }

    PanoProcessState panoProcessState = renderPano(new File("."), config, srcConfigFile,
        DEFAULT_BACKGROUND_COLOR);

    System.out.println(JsonUtils.toJson(panoProcessState));
  }

  /**
   * everything in directory (recursive)
   */
  private static void renderAll(File root)
      throws IOException, ExecutionException, InterruptedException {
    List<PanoDirectory> panoDirectories = PanoSearcher.scanRecursive(root);
    for (PanoDirectory p : panoDirectories) {

      Log.info("Pano Dir: '{}'", p.getRoot().getAbsolutePath());
      boolean rerender = false;
      if (p.getLastRun() == null) {
        Log.debug("No last run: force rendering.");
        rerender = true;
      } else {
        if (!Objects.equals(HashUtils.hashFile(p.getConfigFile()),
            p.getLastRun().getConfigHash())) {
          rerender = true;
          Log.debug("Hast of config doesn't match: force rendering.");
        }

        if (!p.getLastRun().getPanoProcessState().isFinished()) {
          rerender = true;
          Log.debug("Last run unfinished: force rendering.");
        }
      }

      if (rerender) {
        Log.info("Rendering required....");

        renderPano(p.getRoot(), p.getConfig(), p.getConfigFile(), DEFAULT_BACKGROUND_COLOR);
        // TODO log
      } else {
        Log.info("No rendering required. Ignore.");
        // TODO log
      }
    }
  }

  private static File toAboluteFile(File root, String path) {
    File inputImageFile = new File(path);
    if (!inputImageFile.isAbsolute()) {
      inputImageFile = new File(root, path);
    }
    try {
      inputImageFile = inputImageFile.getCanonicalFile();
    } catch (IOException e) {
      LOG.error("getCanonicalFile of '{}'/'{}", root.getAbsolutePath(), path, e);
    }
    return inputImageFile;
  }

  /**
   * one single pano
   */
  private static PanoProcessState renderPano(File root, Config config, File srcConfigFile,
      Pixel backgroundColor)
      throws IOException, InterruptedException, ExecutionException {
    Chronograph appChronograph = Chronograph.start();
    File outputFolder = toAboluteFile(root, config.getOutputFolder());

    PanoProcessState result = new PanoProcessState(outputFolder);

    // +===============================================================
    // | Options
    // +===============================================================

    boolean debug = config.isDebug();

    // Source
    File inputImageFile = toAboluteFile(root, config.getInputConfig().getInputImageFile());

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
    String descriptionFileName = FilenameUtils.getBaseName(inputImageFile.getName()) + ".json";
    File descriptionFile = new File(inputImageFile.getParentFile(), descriptionFileName);
    PanoDescription panoDescription = null;
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
    LOG.info("Create target folder: '{}'", outputFolder.getAbsolutePath());
    FileUtils.forceMkdir(outputFolder);

    // load source
    LOG.info("Load source image: '{}'", inputImageFile.getAbsolutePath());
    String ext = FilenameUtils.getExtension(inputImageFile.getName()).toLowerCase();
    ReadableImage sourceImage;
    Chronograph chronograph = Chronograph.start();
    if ("psd".equals(ext) || "psb".equals(ext)) {
      sourceImage = PSD.of(inputImageFile, ConsoleProgressBar.of(""));
    } else {
      sourceImage = new Img(inputImageFile);
    }
    LOG.info("Loaded source image in '{}'", chronograph.stop());
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
          LOG.error("Could not calculate view", t);
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
      LOG.info(viewCalculator.get().toString());
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
    // | Preview(s)
    // +===============================================================

    // generate cube preview
    if (previewCubeEnabled) {
      LOG.info("Render preview cubemap");
      Chronograph previewChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(previewCubeTarget);
      CubeMapImage cubeMapImage = new CubeMapImage(previewCubeEdge);
      for (Face face : Face.values()) {
        FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face, Progress.DUMMY);
      }
      LOG.info("Rendered preview cubemap in {}", previewChronograph.stop());

      LOG.info("Save preview cube: '{}'", previewCubeTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(cubeMapImage, previewCubeTarget, 0.85f);
      LOG.info("Saved preview cube in: '{}'", previewChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.PREVIEW_CUBIC)
          .with(PanoProcessState.ValueType.FILE, previewCubeTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, previewChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              previewChronograph.getDurationForHuman()));
    }

    // generate Equirectangular preview
    if (previewEquirectangularEnabled) {
      LOG.info("Render preview equirectangular");
      Chronograph previewChronograph = Chronograph.start();
      FileUtils.forceMkdirParent(previewEquirectangularTarget);
      Img scaled = BilinearScaler.scale(source, previewEquirectangularEdge * 2,
          previewEquirectangularEdge);
      LOG.info("Rendered preview equirectangular in '{}'", previewChronograph.stop());

      LOG.info("Save preview equirectangular: '{}'",
          previewEquirectangularTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(scaled, previewEquirectangularTarget, 0.85f);
      LOG.info("Saved preview equirectangular in: '{}'", previewChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.PREVIEW_EQUIRECTANGULAR)
          .with(PanoProcessState.ValueType.FILE, previewEquirectangularTarget)
          .with(PanoProcessState.ValueType.DURATION_MS, previewChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              previewChronograph.getDurationForHuman()));
    }

    // generate scaled original preview
    if (previewScaledOriginalEnabled) {
      LOG.info("Render preview scaled");
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
      LOG.info("Rendered preview scaled in '{}'", previewChronograph.stop());

      LOG.info("Save preview scaled: '{}'", previewScaledOriginalTarget.getAbsolutePath());
      previewChronograph = Chronograph.start();
      ImgUtils.save(scaled, previewScaledOriginalTarget, previewScaledMaxSize);
      LOG.info("Saved preview scaled in: '{}'", previewChronograph.stop());

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

      LOG.info(panoInfo.toString());
      int faceEdge = panoInfo.getSourceFaceEdge();

      Img cubeFace = Img.rectangular(faceEdge);
      int faceCount = 0;
      for (Face face : Face.values()) {
        faceCount++;
        Chronograph faceChronograph = Chronograph.start();

        // render face
        LOG.info("Render face: '{}'({}/6) - {}x{}", face, faceCount, faceEdge, faceEdge);
        Chronograph faceRenderChronograph = Chronograph.start();
        FaceRenderExecutor.renderFace(source, cubeFace, face,
            ConsoleProgressBar.of(String.format("Render %s", face)));
        LOG.info("Render face in '{}'", faceRenderChronograph.stop());
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
            LOG.info("Save cube face: '{}'({}/6) -> {}", face, faceCount,
                faceFile.getAbsolutePath());
            Chronograph cubeFaceSaveChronograph = Chronograph.start();
            FileUtils.forceMkdirParent(faceFile);
            ImgUtils.save(scaledCubeFace, faceFile, null);
            LOG.info("Save cube face in '{}'", cubeFaceSaveChronograph.stop());
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

          LOG.info("Save tiles of '{}'({}/6): {}", face, faceCount, tileCount);
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
          LOG.info("Tiles saved in {}", tileSaveChronograph.stop());
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
            LOG.info("Downscale face '{}'({}/6) to 1/2 = {},{}", face, faceCount, newEdge2,
                newEdge2);
            ConsoleProgressBar downHalfScaleProgress = ConsoleProgressBar.of(
                String.format("Downscale of %s", face));
            scaledCubeFace = DownHalfScaler.scale(scaledCubeFace, downHalfScaleProgress);
            LOG.info("Downscaled face in {}", downscaleChronograph.stop());
            result.addStep(PanoProcessState.Step
                .of(PanoProcessState.StepType.SCALE_LEVEL)
                .with(PanoProcessState.ValueType.LEVEL_INDEX, levelIndex)
                .with(PanoProcessState.ValueType.DURATION_MS, downscaleChronograph.getDurationMs())
                .with(PanoProcessState.ValueType.DURATION_HUMAN,
                    downscaleChronograph.getDurationForHuman()));
          }
        }

        LOG.info("Face '{}' completed in {}", face, faceChronograph.stop());
      }
    }

    // Viewer - Pannellum
    if (config.getViewerConfig().getPannellum().isEnabled()) {
      PannellumConfig pannellumConfig = new PannellumConfig(
          panoInfo.getMaxLevelIndex() + 1,
          panoInfo.getSourceFaceEdge(),
          tileEdge,
          panoDescription);

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
          pannellumConfig.setXMin(pv.getFovX1());
          pannellumConfig.setXMax(pv.getFovX2());
          pannellumConfig.setYMin(pv.getFovY1Inv());
          pannellumConfig.setYMax(pv.getFovY2Inv());
        });
      });

      {
        // pannellum target File
        File viewerPannellumFile = new File(outputFolder,
            config.getViewerConfig().getPannellum().getTarget());
        LOG.info("Render Pannellum html: '{}'", viewerPannellumFile.getAbsolutePath());

        Pannellum pannellum = new Pannellum();
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
        LOG.info("Render PannellumTemplate html: '{}'",
            viewerPannellumTemplateFile.getAbsolutePath());

        PannellumTemplate pannellum = new PannellumTemplate();
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
        LOG.info("Render Marzipano html: '{}'", viewerMarzipanoFile.getAbsolutePath());

        Marzipano marzipano = new Marzipano();
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
        LOG.info("Render MarzipanoTemplate html: '{}'",
            viewerMarzipanoTemplateFile.getAbsolutePath());

        MarzipanoTemplate marzipanoTemplate = new MarzipanoTemplate();
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
      LOG.info("Archived to '{}' in {}", archiveFile.getAbsolutePath(), zipChronograph.stop());
      result.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.ARCHIVE)
          .with(PanoProcessState.ValueType.FILE, archiveFile)
          .with(PanoProcessState.ValueType.DURATION_MS, zipChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN, zipChronograph.getDurationForHuman()));
    }

    LOG.info("Completed in {}", appChronograph.stop());
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
        File configFile = new File(outputFolder, DEFAULT_CONFIG_FILE_NAME);
        if (configFile.exists()) {
          lastRun.setConfigHash(HashUtils.hashFile(configFile));
        }
      }
      File lastRunFile = new File(new File(outputFolder, DEFAULT_LAST_RUN_CONFIG_FOLDER_NAME),
          DEFAULT_LAST_RUN_CONFIG_FILE_NAME);
      FileUtils.forceMkdirParent(lastRunFile);
      JsonUtils.saveJson(lastRunFile, lastRun);
    }

    return result;
  }
}
