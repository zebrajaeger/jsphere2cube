package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.facerenderer.FaceRenderExecutor;
import de.zebrajaeger.sphere2cube.image.CubeMapImage;
import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.image.Img;
import de.zebrajaeger.sphere2cube.image.Pixel;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoUtils;
import de.zebrajaeger.sphere2cube.progress.Progress;
import de.zebrajaeger.sphere2cube.psd.PSD;
import de.zebrajaeger.sphere2cube.scaler.BilinearScaler;
import de.zebrajaeger.sphere2cube.util.ImgUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

class PixelTest {

  @Test
  void loadPSB() throws IOException, InterruptedException {
    StopWatch sw = new StopWatch();

    File dir = new File("/home/l/prj/panos");
    File f_500M = new File(dir, "IMG_1766_S(168.00x25.63(8.99)).psb");
    File f_1000M = new File(dir, "IMG_1757_S(152.77x46.56(3.20)).psb");
    File f_5000M = new File(dir,
        "(IMG_9494-IMG_9603-110)-{d=S-360.00x137.92(-9.87)}-{p=IMG_9494_IMG_9603-110 (2011-07-12)}.psb");

    sw.start();
    PSD psd = PSD.of(f_5000M, null);

    sw.stop();

    System.out.print(sw.getTime(TimeUnit.MILLISECONDS));
  }

  @Test
  void scaleUp() throws IOException {
    Img img1 = new Img(2, 2);
    img1.setPixel(0, 0, new Pixel(255, 255, 255));
    //img1.setPixel(1,1, new Pixel(255,0,0));
    ImgUtils.saveAsPng(img1, new File("test1.png"));
    Img img2 = img1.scaleBilinear(12.3f);
    ImgUtils.saveAsPng(img2, new File("test2.png"));
  }

  @Test
  void scaleDown() throws IOException {
    Img img1 = new Img(new File("Hagenbrunnen_Frittlingen.png"));
    ImgUtils.saveAsJpg(img1, new File("test3.jpg"), 0.3f);
    Img img2 = img1.scaleBilinear(0.3f);
    ImgUtils.saveAsJpg(img2, new File("test4.jpg"), 0.3f);
  }

  @Test
  void bilinearScaler() throws IOException, InterruptedException {
    Img img1 = new Img(new File("/home/l/Dokumente/sphere2cube/7Lo6s.jpg"));
    BilinearScaler s = new BilinearScaler();
    Img scaled = BilinearScaler.scale(img1, 2000, 250);
    ImgUtils.saveAsJpg(scaled, new File("test_scaled.jpg"), 1f);
  }

  @Test
  void panoInfo() throws IOException {
    Img img1 = new Img(new File("/home/l/Dokumente/sphere2cube/7Lo6s.jpg"));
    EquirectangularImage source = EquirectangularImage.of(img1, 360d, Pixel.black());

    PanoInfo panoInfo = PanoUtils.calcPanoInfo(source, 100);
    System.out.println(panoInfo);
  }

  @Test
  void cubeMapImage() throws IOException, InterruptedException {
    Img img1 = new Img(new File("/home/l/Dokumente/sphere2cube/7Lo6s.jpg"));
    EquirectangularImage source = EquirectangularImage.of(img1, 360d, Pixel.black());

    CubeMapImage cubeMapImage = new CubeMapImage(500);
    for (Face face : Face.values()) {
      FaceRenderExecutor.renderFace(source, cubeMapImage.getFaceImg(face), face, Progress.DUMMY);
    }
    ImgUtils.saveAsJpg(cubeMapImage, new File("test_cubemap.jpg"), 1f);
  }

  @Test
  void fileTest() {
    File f = new File("*abc");
    System.out.println(f.getAbsolutePath());
  }

  @Test
  void imgSize() {
    System.out.println(PSD.toImageSize(100));
    System.out.println(PSD.toImageSize(1024));
    System.out.println(PSD.toImageSize(1024 * 1024));
    System.out.println(PSD.toImageSize(1024 * 1024 * 1024));
    System.out.println(PSD.toImageSize(1024L * 1024 * 1024 * 22));
  }

}
