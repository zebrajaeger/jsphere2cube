package de.zebrajaeger.sphere2cube.facerenderer;

import de.zebrajaeger.sphere2cube.image.EquirectangularImage;
import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.image.Pixel;
import net.jafama.FastMath;


public class FaceRenderer {
    private final EquirectangularImage source;
    private final double sourceEdge;

    public FaceRenderer(EquirectangularImage source) {
        this.source = source;
        sourceEdge = source.getWidth() / 4d;
    }

    public void calculatePixel(boolean invertX, boolean invertY, int i, int j, Face face, double targetEdge, Pixel result) {
        double a = 2d * (double) i / targetEdge;
        if (invertX) {
            a = 2d - a;
        }

        double b = 2d * (double) j / targetEdge;
        if (invertY) {
            b = 2d - b;
        }

        double x, y, z;
        switch (face) {
            case BACK -> {
                x = -1d;
                y = 1d - a;
                z = 1d - b;
            }
            case LEFT -> {
                x = a - 1d;
                y = -1d;
                z = 1d - b;
            }
            case FRONT -> {
                x = 1d;
                y = a - 1d;
                z = 1d - b;
            }
            case RIGHT -> {
                x = 1d - a;
                y = 1d;
                z = 1d - b;
            }
            case UP -> {
                x = 1d - b;
                y = a - 1d;
                z = 1d;
            }
            case DOWN -> {
                x = 1d - b;
                y = a - 1d;
                z = -1d;
            }
            default -> throw new RuntimeException("Unknown face:" + face);
        }

        // TODO these 3 lines costs ~70% CPU of this method. Maybe run on GPU?
        double theta = FastMath.atan2(y, x);
        double r = FastMath.hypot(x, y);
        double phi = FastMath.atan2(z, r);

        // source img coords
        double uf = (2d * sourceEdge * (theta + FastMath.PI) / FastMath.PI);
        double vf = (2d * sourceEdge * (FastMath.PI / 2d - phi) / FastMath.PI);

        // Use bilinear interpolation between the four surrounding pixels
        int u1 = (int) FastMath.floor(uf);  // coords of pixel to bottom left
        int v1 = (int) FastMath.floor(vf);
        double mu = uf - (float) u1;      // fraction of way across pixel
        double nu = vf - (float) v1;
        // TODO run on GPU until here?

        int u2 = u1 + 1;       // coords of pixel to top right
        int v2 = v1 + 1;

        int xMax = source.getWidth();
        int yMax = source.getHeight();
        u1 %= xMax;
        u2 %= xMax;
        v1 %= yMax;
        v2 %= yMax;

        // Pixel values of four corners
        Pixel p1 = source.getPixel(u1, v1);
        Pixel p2 = source.getPixel(u2, v1);
        Pixel p3 = source.getPixel(u1, v2);
        Pixel p4 = source.getPixel(u2, v2);

        double c12r = (p1.r * (1 - mu)) + (p2.r * mu);
        double c34r = (p3.r * (1 - mu)) + (p4.r * mu);
        double c1234r = (c12r * (1 - nu)) + (c34r * nu);
        result.r = (int) FastMath.round(c1234r);

        double c12g = (p1.g * (1 - mu)) + (p2.g * mu);
        double c34g = (p3.g * (1 - mu)) + (p4.g * mu);
        double c1234g = (c12g * (1 - nu)) + (c34g * nu);
        result.g = (int) FastMath.round(c1234g);

        double c12b = (p1.b * (1 - mu)) + (p2.b * mu);
        double c34b = (p3.b * (1 - mu)) + (p4.b * mu);
        double c1234b = (c12b * (1 - nu)) + (c34b * nu);
        result.b = (int) FastMath.round(c1234b);
    }
}
