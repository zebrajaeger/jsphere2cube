package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.names.CubeFaceNameGenerator;
import de.zebrajaeger.sphere2cube.names.TileNameGenerator;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        // +===============================================================
        // | Options
        // +===============================================================

        // Source
        File inputImageFile = new File("/home/l/Dokumente/sphere2cube/7Lo6s.jpg");
        double inputImageHorizontalAngel = 360d;

        File outputFolder = new File("./temp");

        // Preview - CubeMap
        boolean previewCubeEnabled = false;
        int previewCubeEdge = 200;
        File previewCubeTarget = new File(outputFolder, "preview_cube.jpg");

        // Preview Equirectangular
        boolean previewEquirectangularEnabled = false;
        int previewEquirectangularEdge = 200;
        File previewEquirectangularTarget = new File(outputFolder, "preview_equirectangular.jpg");

        // Preview Scaled
        boolean previewScaledOriginalEnabled = false;
        int previewScaledOriginalEdge = 200;
        File previewScaledOriginalTarget = new File(outputFolder, "preview_scaled.jpg");

        // Cube faces
        boolean cubeMapFacesEnabled = true;
        boolean cubeMapTilesEnabled = true;
        // levelCount, levelIndex, inverseLevelCount, inverseLevelIndex
        // faceNameUpperCase, faceNameLowerCase, faceShortNameUpperCase, faceShortNameLowerCase,
        // xIndex, xCount, yIndex, yCount
        String cubeFaceTarget = "{{faceNameLowerCase}}.jpg";
        String cubeFaceTilesTarget = "{{levelCount}}/{{faceNameShortLowerCase}}{{xIndex}}_{{yIndex}}.jpg";
        int tileEdge = 64;

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
            PanoInfo panoInfo = PanoUtils.calcPanoInfo(source, tileEdge);
            LOG.info(panoInfo.toString());
            int faceEdge = panoInfo.getSourceFaceEdge();

            Img cubeFace = new Img(faceEdge, faceEdge);
            for (Face face : Face.values()) {
                LOG.info("Render face: '{}' - {}x{}", face, faceEdge, faceEdge);
                FaceRenderExecutor.renderFace(source, cubeFace, face);
                File f = new File(outputFolder, cubeFaceNameGenerator.generate(face));

                LOG.info("Save cube face: '{}' -> {}", face, f.getAbsolutePath());
                ImgUtils.save(cubeFace, f, 0.85f);
            }

        }
    }
}
