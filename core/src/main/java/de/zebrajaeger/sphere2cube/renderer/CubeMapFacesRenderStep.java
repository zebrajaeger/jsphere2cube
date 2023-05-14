package de.zebrajaeger.sphere2cube.renderer;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.PanoProcessState.ValueType;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.multithreading.MaxJobQueueExecutor;
import de.zebrajaeger.sphere2cube.names.CubeFaceNameGenerator;
import de.zebrajaeger.sphere2cube.names.TileNameGenerator;
import de.zebrajaeger.sphere2cube.pano.PanoLevel;
import de.zebrajaeger.sphere2cube.progress.ConsoleProgressBar;
import de.zebrajaeger.sphere2cube.scaler.DownHalfScaler;
import de.zebrajaeger.sphere2cube.tiles.TileSaveJob;
import de.zebrajaeger.sphere2cube.util.Chronograph;
import de.zebrajaeger.sphere2cube.util.ImgUtils;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class CubeMapFacesRenderStep implements RenderStep {

  @Override
  public boolean isEnabled(RenderContext renderContext) {
    boolean cubeMapFacesEnabled = renderContext.getConfig().getCubeMapConfig().getFaces()
        .isEnabled();
    boolean cubeMapTilesEnabled = renderContext.getConfig().getCubeMapConfig().getTiles()
        .isEnabled();
    return cubeMapFacesEnabled || cubeMapTilesEnabled;
  }

  @Override
  public void render(RenderContext renderContext) throws RenderException {
    String cubeFaceTarget = renderContext.getConfig().getCubeMapConfig().getFaces().getTarget();
    String cubeFaceTilesTarget = renderContext.getConfig().getCubeMapConfig().getTiles()
        .getTarget();

    CubeFaceNameGenerator cubeFaceNameGenerator = new CubeFaceNameGenerator(cubeFaceTarget);
    TileNameGenerator tileNameGenerator = new TileNameGenerator(cubeFaceTilesTarget);

    log.info(renderContext.getPanoInfo().toString());
    int faceEdge = renderContext.getPanoInfo().getSourceFaceEdge();

    Img cubeFace = Img.rectangular(faceEdge);
    int faceCount = 0;
    for (Face face : Face.values()) {
      faceCount++;
      Chronograph faceChronograph = Chronograph.start();

      // render face
      log.info("Render face: '{}'({}/6) - {}x{}", face, faceCount, faceEdge, faceEdge);
      Chronograph faceRenderChronograph = Chronograph.start();
      try {
        FaceRenderExecutor.renderFace(renderContext.getSource(), cubeFace, face,
            ConsoleProgressBar.of(String.format("Render %s", face)));
      } catch (InterruptedException e) {
        throw new RenderException("Could not render face", e);
      }
      log.info("Render face in '{}'", faceRenderChronograph.stop());
      renderContext.addStep(PanoProcessState.Step
          .of(PanoProcessState.StepType.FACE)
          .with(PanoProcessState.ValueType.FACE, face)
          .with(PanoProcessState.ValueType.DURATION_MS, faceRenderChronograph.getDurationMs())
          .with(PanoProcessState.ValueType.DURATION_HUMAN,
              faceRenderChronograph.getDurationForHuman()));
      if (renderContext.getConfig().isDebug()) {
        ImgUtils.drawBorder(cubeFace, face.getColor());
      }

      Img scaledCubeFace = cubeFace;
      for (int levelIndex = renderContext.getPanoInfo().getMaxLevelIndex(); levelIndex >= 0;
          --levelIndex) {
        PanoLevel level = renderContext.getPanoInfo().getLevel(levelIndex);

        // save face image
        boolean cubeMapFacesEnabled = renderContext.getConfig().getCubeMapConfig().getFaces()
            .isEnabled();
        if (cubeMapFacesEnabled) {
          File faceFile = new File(renderContext.getOutputFolder(),
              cubeFaceNameGenerator.generate(renderContext.getPanoInfo(), levelIndex, face));
          log.info("Save cube face: '{}'({}/6) -> {}", face, faceCount,
              faceFile.getAbsolutePath());
          Chronograph cubeFaceSaveChronograph = Chronograph.start();
          try {
            FileUtils.forceMkdirParent(faceFile);
          } catch (IOException e) {
            throw new RenderException(
                "Could not create faceFile directories: " + faceFile.getAbsolutePath(), e);
          }
          try {
            ImgUtils.save(scaledCubeFace, faceFile, null);
          } catch (IOException e) {
            throw new RenderException(
                "Could not save scaled cube face: " + faceFile.getAbsolutePath(), e);
          }
          log.info("Save cube face in '{}'", cubeFaceSaveChronograph.stop());
          renderContext.addStep(PanoProcessState.Step
              .of(PanoProcessState.StepType.SAVE_FACE)
              .with(ValueType.TARGET_FILE, faceFile)
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
        int tileEdge = renderContext.getConfig().getCubeMapConfig().getTiles().getTileEdge();
        for (int yIndex = 0; yIndex < lineTileCount; ++yIndex) {
          for (int xIndex = 0; xIndex < lineTileCount; ++xIndex) {
            String name = tileNameGenerator.generate(renderContext.getPanoInfo(), levelIndex, face,
                xIndex, yIndex);

            tileExecutor.addJob(new TileSaveJob(
                scaledCubeFace,
                new File(renderContext.getOutputFolder(), name),
                tileEdge,
                xIndex * tileEdge,
                yIndex * tileEdge,
                renderContext.getConfig().isDebug()));
            tileProgressBar.update((long) yIndex * lineTileCount + xIndex);
          }
        }
        try {
          tileExecutor.shutdown();
        } catch (InterruptedException e) {
          throw new RenderException("Could not shutdown tileExecutor", e);
        }
        tileProgressBar.finish();
        log.info("Tiles saved in {}", tileSaveChronograph.stop());
        renderContext.addStep(PanoProcessState.Step
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
          try {
            scaledCubeFace = DownHalfScaler.scale(scaledCubeFace, downHalfScaleProgress);
          } catch (InterruptedException e) {
            throw new RenderException("Could not scale cube face", e);
          }
          log.info("Downscaled face in {}", downscaleChronograph.stop());
          renderContext.addStep(PanoProcessState.Step
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
}
