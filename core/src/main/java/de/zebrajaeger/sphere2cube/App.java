package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.names.CubeFaceNameGenerator;
import de.zebrajaeger.sphere2cube.names.TileNameGenerator;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoLevel;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.scaler.DownHalfScaler;
import de.zebrajaeger.sphere2cube.viewer.PanellumConfig;
import de.zebrajaeger.sphere2cube.viewer.Pannellum;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        if (args.length == 0) {
            File src = new File("sphere2cube.json");
            if (!src.exists()) {
                Config.help();
                System.exit(-1);
            }
        } else if (args.length == 1) {
            File src = new File(args[0]);
            if (!src.exists()) {
                Config.help();
                System.exit(-1);
            }
        }

        Config config = Config.of(args);
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
        Img sourceImage = new Img(inputImageFile);
        EquirectangularImage source = EquirectangularImage.of(sourceImage, inputImageHorizontalAngel);

        PanoInfo panoInfo = PanoUtils.calcPanoInfo(source, tileEdge);

        // +===============================================================
        // | Preview(s)
        // +===============================================================

        // generate cube preview
        if (previewCubeEnabled) {
            LOG.info("Render preview cubemap: '{}'", previewCubeTarget.getAbsolutePath());
            FileUtils.forceMkdirParent(previewCubeTarget);
            CubeMapImage cubeMapImage = new CubeMapImage(previewCubeEdge);
            for (Face face : Face.values()) {
                FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face);
            }
            ImgUtils.save(cubeMapImage, previewCubeTarget, 0.85f);
        }

        // generate Equirectangular preview
        if (previewEquirectangularEnabled) {
            LOG.info("Render equirectangular: '{}'", previewEquirectangularTarget.getAbsolutePath());
            FileUtils.forceMkdirParent(previewEquirectangularTarget);
            BilinearScaler scaler = new BilinearScaler();
            Img scaled = scaler.scale(source, previewEquirectangularEdge * 2, previewEquirectangularEdge);
            ImgUtils.save(scaled, previewEquirectangularTarget, 0.85f);
        }

        // generate scaled original preview
        if (previewScaledOriginalEnabled) {
            LOG.info("Render preview scaled: '{}'", previewScaledOriginalTarget.getAbsolutePath());
            FileUtils.forceMkdirParent(previewScaledOriginalTarget);

            BilinearScaler scaler = new BilinearScaler();
            float factor;
            if (sourceImage.getWidth() > sourceImage.getHeight()) {
                factor = (float) sourceImage.getWidth() / (float) previewScaledOriginalEdge;
            } else {
                factor = (float) sourceImage.getHeight() / (float) previewScaledOriginalEdge;
            }
            Img scaled = scaler.scale(sourceImage, (int) (source.getWidth() / factor), (int) (source.getHeight() / factor));
            ImgUtils.save(scaled, previewScaledOriginalTarget, 0.85f);
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
                FaceRenderExecutor.renderFace(source, cubeFace, face);
                if (debug) {
                    ImgUtils.drawBorder(cubeFace, face.getColor());
                }

                if (cubeMapTilesEnabled) {
                    Img scaledCubeFace = cubeFace;
                    DownHalfScaler downHalfScaler = new DownHalfScaler();
                    Img tile = Img.rectangular(tileEdge);
                    for (int levelIndex = panoInfo.getMaxLevelIndex(); levelIndex >= 0; --levelIndex) {

                        if (cubeMapFacesEnabled) {
                            File faceFile = new File(outputFolder, cubeFaceNameGenerator.generate(panoInfo, levelIndex, face));
                            LOG.info("Save cube face: '{}' -> {}", face, faceFile.getAbsolutePath());
                            FileUtils.forceMkdirParent(faceFile);
                            ImgUtils.save(scaledCubeFace, faceFile, null);
                        }
                        PanoLevel level = panoInfo.getLevel(levelIndex);

                        // render tiles for face and level
                        int tileCount = level.getTileCount();
                        for (int yIndex = 0; yIndex < tileCount; ++yIndex) {
                            for (int xIndex = 0; xIndex < tileCount; ++xIndex) {
                                scaledCubeFace.copyTo(tile, xIndex * tileEdge, yIndex * tileEdge);
                                String name = tileNameGenerator.generate(panoInfo, levelIndex, face, xIndex, yIndex);
                                File tileFile = new File(outputFolder, name);
                                LOG.info("Save tile: '{}'-'{}'-{},{} -> {}", levelIndex, face, xIndex, yIndex, tileFile.getAbsolutePath());
                                FileUtils.forceMkdirParent(tileFile);
                                if (debug) {
                                    ImgUtils.drawDottedBorder(tile, Pixel.of(0xffffff));
                                }
                                ImgUtils.save(tile, tileFile, null);
                            }
                        }

                        // downscale cube face image
                        if (levelIndex > 0) {
                            int newEdge2 = level.getFaceEdge() / 2;
                            LOG.info("Downscale face to 1/2 = {},{}", newEdge2, newEdge2);
                            scaledCubeFace = downHalfScaler.scale(scaledCubeFace);
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
    }
}
