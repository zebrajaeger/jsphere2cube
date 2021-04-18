package de.zebrajaeger.sphere2cube;

public class CubeMapImage implements ReadableImage {
    private final Img img;
    private final int cubeEdge;

    public CubeMapImage(int cubeEdge) {
        this.cubeEdge = cubeEdge;
        img = new Img(cubeEdge * 4, cubeEdge * 3);
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @Override
    public void getPixel(int x, int y, Pixel target) {
        img.getPixel(x, y, target);
    }

    @Override
    public Pixel getPixel(int x, int y) {
        return img.getPixel(x, y);
    }

    @Override
    public int getRGB(int x, int y) {
        return img.getRGB(x, y);
    }

    public ReadableImage getImg() {
        return img;
    }

    public WriteableImage getFaceImg(Face face) {
        return switch (face) {
            case BACK -> new FaceImage(this, false, false, 0, cubeEdge);
            case LEFT -> new FaceImage(this, false, false, cubeEdge, cubeEdge);
            case FRONT -> new FaceImage(this, false, false, cubeEdge * 2, cubeEdge);
            case RIGHT -> new FaceImage(this, false, false, cubeEdge * 3, cubeEdge);
            case UP -> new FaceImage(this, false, true, cubeEdge * 2, 0);
            case DOWN -> new FaceImage(this, false, false, cubeEdge * 2, cubeEdge * 2);
        };
    }

    class FaceImage implements WriteableImage {
        private final CubeMapImage cubeMapImage;
        private final Img img;
        private final int cubeEdgeOffset;
        private final boolean xInverse;
        private final boolean yInverse;
        private final int xOffset;
        private final int yOffset;


        public FaceImage(CubeMapImage cubeMapImage, boolean xInverse, boolean yInverse, int xOffset, int yOffset) {
            this.cubeMapImage = cubeMapImage;
            this.img = cubeMapImage.img;
            this.cubeEdgeOffset = this.cubeMapImage.cubeEdge - 1;
            this.xInverse = xInverse;
            this.yInverse = yInverse;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        @Override
        public int getWidth() {
            return cubeMapImage.cubeEdge;
        }

        @Override
        public int getHeight() {
            return cubeMapImage.cubeEdge;
        }

        @Override
        public void setPixel(int x, int y, Pixel rgb) {
            if (xInverse) {
                if (yInverse) {
                    // both
                    img.setPixel(cubeEdgeOffset - x + xOffset, cubeEdgeOffset - y + yOffset, rgb);
                } else {
                    // x inverse
                    img.setPixel(cubeEdgeOffset - x + xOffset, y + yOffset, rgb);
                }
            } else {
                if (yInverse) {
                    // y inverse
                    img.setPixel(x + xOffset, cubeEdgeOffset - y + yOffset, rgb);
                } else {
                    // no inversion
                    img.setPixel(x + xOffset, y + yOffset, rgb);
                }
            }
        }
    }
}
