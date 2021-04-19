package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.multithreading.JobExecutor;
import de.zebrajaeger.sphere2cube.names.CubeFaceNameGenerator;
import de.zebrajaeger.sphere2cube.names.TileNameGenerator;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoLevel;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.scaler.DownHalfScaler;
import de.zebrajaeger.sphere2cube.tiles.TileSaveJob;
import de.zebrajaeger.sphere2cube.viewer.PanellumConfig;
import de.zebrajaeger.sphere2cube.viewer.Pannellum;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Chronograph appChronograph = Chronograph.start();
        Config config;

        if (args.length == 0) {
            File src = new File("sphere2cube.json");
            if (!src.exists()) {
                Config.help();
                System.exit(-1);
                return;
            } else {
                config = Config.of(src);
            }
        } else if (args.length == 1) {
            File src = new File(args[0]);
            if (!src.exists()) {
                Config.help();
                System.exit(-1);
                return;
            } else {
                config = Config.of(src);
            }
        } else {
            config = Config.of(args);
        }

        if (config.getSaveConfig().isSaveConfig()) {
            File t = new File(config.getSaveConfig().getSaveConfigTarget());
            FileUtils.forceMkdirParent(t);
            FileUtils.write(t, config.toJson(), StandardCharsets.UTF_8);
        }

        // +===============================================================
        // | Options
        // +===============================================================

        boolean debug = config.isDebug();

        // Source
        File inputImageFile = config.getInputConfig().getInputImageFile();
        double inputImageHorizontalAngel = config.getInputConfig().getInputImageHorizontalAngel();

        File outputFolder = config.getOutputFolder();

        // Preview - CubeMap
        boolean previewCubeEnabled = config.getPreviewsConfig().getCubeMapPreview().isEnabled();
        int previewCubeEdge = config.getPreviewsConfig().getCubeMapPreview().getEdge();
        File previewCubeTarget = new File(outputFolder, "preview_cube.jpg");

        // Preview Equirectangular
        boolean previewEquirectangularEnabled = config.getPreviewsConfig().getCubeMapPreview().isEnabled();
        int previewEquirectangularEdge = config.getPreviewsConfig().getCubeMapPreview().getEdge();
        File previewEquirectangularTarget = new File(outputFolder, "preview_equirectangular.jpg");

        // Preview Scaled
        boolean previewScaledOriginalEnabled = config.getPreviewsConfig().getScaledPreview().isEnabled();
        int previewScaledOriginalEdge = config.getPreviewsConfig().getScaledPreview().getEdge();
        File previewScaledOriginalTarget = new File(outputFolder, "preview_scaled.jpg");

        // Cube faces
        boolean cubeMapFacesEnabled = config.getCubeMapConfig().getFaces().isEnabled();
        String cubeFaceTarget = config.getCubeMapConfig().getFaces().getTarget();

        boolean cubeMapTilesEnabled = config.getCubeMapConfig().getTiles().isEnabled();
        String cubeFaceTilesTarget = config.getCubeMapConfig().getTiles().getTarget();
        int tileEdge = config.getCubeMapConfig().getTiles().getTileEdge();

        // viewer
        boolean viewerPannellumEnabled = config.getViewerConfig().getPannellum().isEnabled();
        File viewerPannellumFile = new File(outputFolder, config.getViewerConfig().getPannellum().getTarget());

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
            sourceImage = PSD.of(inputImageFile);
        } else {
            sourceImage = new Img(inputImageFile);
        }
        LOG.info("Loaded source image in '{}'", chronograph.stop());
        EquirectangularImage source = EquirectangularImage.of(sourceImage, inputImageHorizontalAngel);

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
                FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face);
            }
            LOG.info("Rendered preview cubemap in {}", previewChronograph.stop());

            LOG.info("Save preview cube: '{}'", previewCubeTarget.getAbsolutePath());
            previewChronograph = Chronograph.start();
            ImgUtils.save(cubeMapImage, previewCubeTarget, 0.85f);
            LOG.info("Saved preview cube in: '{}'", previewChronograph.stop());
        }

        // generate Equirectangular preview
        if (previewEquirectangularEnabled) {
            LOG.info("Render preview equirectangular");
            Chronograph previewChronograph = Chronograph.start();
            FileUtils.forceMkdirParent(previewEquirectangularTarget);
            Img scaled = BilinearScaler.scale(source, previewEquirectangularEdge * 2, previewEquirectangularEdge);
            LOG.info("Rendered preview equirectangular in '{}'", previewChronograph.stop());

            LOG.info("Save preview equirectangular: '{}'", previewEquirectangularTarget.getAbsolutePath());
            previewChronograph = Chronograph.start();
            ImgUtils.save(scaled, previewEquirectangularTarget, 0.85f);
            LOG.info("Saved preview equirectangular in: '{}'", previewChronograph.stop());
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
            Img scaled = BilinearScaler.scale(sourceImage, (int) (source.getWidth() / factor), (int) (source.getHeight() / factor));
            LOG.info("Rendered preview scaled in '{}'", previewChronograph.stop());

            LOG.info("Save preview scaled: '{}'", previewScaledOriginalTarget.getAbsolutePath());
            previewChronograph = Chronograph.start();
            ImgUtils.save(scaled, previewScaledOriginalTarget, 0.85f);
            LOG.info("Saved preview scaled in: '{}'", previewChronograph.stop());
        }

        // cube map faces
        if (cubeMapFacesEnabled || cubeMapTilesEnabled) {
            CubeFaceNameGenerator cubeFaceNameGenerator = new CubeFaceNameGenerator(cubeFaceTarget);
            TileNameGenerator tileNameGenerator = new TileNameGenerator(cubeFaceTilesTarget);

            LOG.info(panoInfo.toString());
            int faceEdge = panoInfo.getSourceFaceEdge();

            Img cubeFace = Img.rectangular(faceEdge);
            for (Face face : Face.values()) {
                LOG.info("Render face: '{}' - {}x{}", face, faceEdge, faceEdge);
                Chronograph faceChronograph = Chronograph.start();
                FaceRenderExecutor.renderFace(source, cubeFace, face);
                LOG.info("Render face in '{}'", faceChronograph.stop());
                if (debug) {
                    ImgUtils.drawBorder(cubeFace, face.getColor());
                }

                if (cubeMapTilesEnabled || cubeMapFacesEnabled) {
                    Img scaledCubeFace = cubeFace;
                    for (int levelIndex = panoInfo.getMaxLevelIndex(); levelIndex >= 0; --levelIndex) {

                        if (cubeMapFacesEnabled) {
                            File faceFile = new File(outputFolder, cubeFaceNameGenerator.generate(panoInfo, levelIndex, face));
                            LOG.info("Save cube face: '{}' -> {}", face, faceFile.getAbsolutePath());
                            Chronograph cubeFaceSaveChronograph = Chronograph.start();
                            FileUtils.forceMkdirParent(faceFile);
                            ImgUtils.save(scaledCubeFace, faceFile, null);
                            LOG.info("Save cube face in '{}'", cubeFaceSaveChronograph.stop());
                        }
                        PanoLevel level = panoInfo.getLevel(levelIndex);

                        // render tiles for face and level
                        int tileCount = level.getTileCount();

                        LOG.info("Save tiles");
                        Chronograph tileSaveChronograph = Chronograph.start();
                        JobExecutor tsc = new JobExecutor();
                        for (int yIndex = 0; yIndex < tileCount; ++yIndex) {
                            for (int xIndex = 0; xIndex < tileCount; ++xIndex) {
                                String name = tileNameGenerator.generate(panoInfo, levelIndex, face, xIndex, yIndex);

                                tsc.addJob(new TileSaveJob(
                                        scaledCubeFace,
                                        new File(outputFolder, name),
                                        tileEdge,
                                        xIndex * tileEdge,
                                        yIndex * tileEdge,
                                        debug));
                            }
                        }
                        tsc.shutdown();
                        LOG.info("Tiles saved in {}", tileSaveChronograph.stop());

                        // downscale cube face image
                        if (levelIndex > 0) {
                            Chronograph downscaleChronograph = Chronograph.start();
                            int newEdge2 = level.getFaceEdge() / 2;
                            LOG.info("Downscale face to 1/2 = {},{}", newEdge2, newEdge2);
                            scaledCubeFace = DownHalfScaler.scale(scaledCubeFace);
                            LOG.info("Downscaled face in {}", downscaleChronograph.stop());
                        }
                    }
                }
            }
        }

        if (viewerPannellumEnabled) {
            LOG.info("Render pannellum html: '{}'", viewerPannellumFile.getAbsolutePath());
            PanellumConfig pannellumConfig = new PanellumConfig(panoInfo.getMaxLevelIndex() + 1, panoInfo.getSourceFaceEdge(), tileEdge);
            Pannellum pannellum = new Pannellum();
            String html = pannellum.render(pannellumConfig);
            FileUtils.write(viewerPannellumFile, html, StandardCharsets.UTF_8);
        }

        LOG.info("Completed in {}", appChronograph.stop());
    }
}
