package de.zebrajaeger.sphere2cube;

public class EquirectangularImage implements ReadableImage {

  private final int width;
  private final ReadableImage embeddedImage;
  private final Pixel backgroundColor;

  private final int insideX1;
  private final int insideX2;
  private final int insideY1;
  private final int insideY2;


  static EquirectangularImage of(ReadableImage embeddedImage, double embeddedWidthAngel,
      double verticalOffset, Pixel backgroundColor) {
    int fullWidth = (int) (360d * embeddedImage.getWidth() / embeddedWidthAngel);
    int fullHeight = fullWidth / 2;

    int shiftY = (int) (fullHeight * verticalOffset / 180d);

    int offX = (fullWidth - embeddedImage.getWidth()) / 2;
    int offY = ((fullHeight - embeddedImage.getHeight()) / 2) + shiftY;

    return new EquirectangularImage(fullWidth, embeddedImage, offX, offY, backgroundColor);
  }

  @Deprecated
  static EquirectangularImage of(ReadableImage embeddedImage, double embeddedWidthAngel,
      Pixel backgroundColor) {
    int fullWidth = (int) (360d * embeddedImage.getWidth() / embeddedWidthAngel);
    int fullHeight = fullWidth / 2;

    int offX = (fullWidth - embeddedImage.getWidth()) / 2;
    int offY = (fullHeight - embeddedImage.getHeight()) / 2;

    return new EquirectangularImage(fullWidth, embeddedImage, offX, offY, backgroundColor);
  }

  public EquirectangularImage(int width, ReadableImage embeddedImage, int offX, int offY,
      Pixel backgroundColor) {
    this.backgroundColor = backgroundColor;
    this.width = width;
    this.embeddedImage = embeddedImage;
    insideX1 = offX;
    insideX2 = offX + embeddedImage.getWidth();
    insideY1 = offY;
    insideY2 = offY + embeddedImage.getHeight();
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return width / 2;
  }

  @Override
  public void getPixel(int x, int y, Pixel target) {
    if (x >= insideX1 && x < insideX2 && y >= insideY1 && y < insideY2) {
      embeddedImage.getPixel(x - insideX1, y - insideY1, target);
    } else {
      target.r = backgroundColor.r;
      target.g = backgroundColor.g;
      target.b = backgroundColor.b;
    }
  }

  @Override
  public Pixel getPixel(int x, int y) {
    if (x >= insideX1 && x < insideX2 && y >= insideY1 && y < insideY2) {
      return embeddedImage.getPixel(x - insideX1, y - insideY1);
    } else {
      return backgroundColor;
    }
  }

  @Override
  public int getRGB(int x, int y) {
//        if (x >= insideX1 && x < insideX2 && y >= insideY1 && y < insideY2) {
//            return embeddedImage.getRGB(x - insideX1, y - insideY1);
//        } else {
//            return spaceRGB;
//        }
    return 0;
  }

  @Override
  public String toString() {
    return "EquirectangularImage{" +
        "width=" + width +
        ", insideX1=" + insideX1 +
        ", insideX2=" + insideX2 +
        ", insideY1=" + insideY1 +
        ", insideY2=" + insideY2 +
        '}';
  }
}
