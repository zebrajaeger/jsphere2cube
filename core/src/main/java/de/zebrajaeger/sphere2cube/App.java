package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        // Options
        File inputImageFile = new File("/home/l/Dokumente/sphere2cube/7Lo6s.jpg");
        double inputImageHorizontalAngel = 180d;

        File outputFolder = new File("./temp");

        boolean previewCubeEnabled = true;
        int previewCubeEdge = 200;
        File previewCubeTarget = new File(outputFolder,"preview_cube.jpg");

        boolean previewEquirectangularEnabled = true;
        int previewEquirectangularEdge = 200;
        File previewEquirectangularTarget = new File(outputFolder,"preview_equirectangular.jpg");

        boolean previewScaledOriginalEnabled = true;
        int previewScaledOriginalEdge = 200;
        File previewScaledOriginalTarget = new File(outputFolder,"preview_scaled.jpg");


        // ensure output folder
        LOG.info("Create target folder: '{}'", outputFolder.getAbsolutePath());
        FileUtils.forceMkdir(outputFolder);

        // load source
        LOG.info("Load source image: '{}'", inputImageFile.getAbsolutePath());
        Img sourceImage = new Img(inputImageFile);
        EquirectangularImage source = EquirectangularImage.of(sourceImage, inputImageHorizontalAngel);

        // generate cube preview
        if(previewCubeEnabled) {
            LOG.info("Render preview cubemap: '{}'", previewCubeTarget.getAbsolutePath());
            CubeMapImage cubeMapImage = new CubeMapImage(previewCubeEdge);
            for (Face face : Face.values()) {
                FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face);
            }
            ImgUtils.save(cubeMapImage, previewCubeTarget, 0.85f);
        }

        // generate Equirectangular preview
        if(previewEquirectangularEnabled) {
            LOG.info("Render equirectangular: '{}'", previewEquirectangularTarget.getAbsolutePath());
            BilinearScaler scaler = new BilinearScaler();
            Img scaled = scaler.scale(source, previewEquirectangularEdge * 2, previewEquirectangularEdge);
            ImgUtils.save(scaled, previewEquirectangularTarget, 0.85f);
        }

        // generate scaled original preview
        if(previewScaledOriginalEnabled) {
            LOG.info("Render preview scaled: '{}'", previewScaledOriginalTarget.getAbsolutePath());
            BilinearScaler scaler = new BilinearScaler();
            float factor;
            if(sourceImage.getWidth()>sourceImage.getHeight()){
                factor = (float)sourceImage.getWidth() / (float) previewScaledOriginalEdge;
            }else{
                factor = (float)sourceImage.getHeight() / (float) previewScaledOriginalEdge;
            }
            Img scaled = scaler.scale(sourceImage, (int)(source.getWidth() / factor), (int)(source.getHeight()/factor));
            ImgUtils.save(scaled, previewScaledOriginalTarget, 0.85f);
        }
    }
}
