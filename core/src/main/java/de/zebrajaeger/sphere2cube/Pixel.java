package de.zebrajaeger.sphere2cube;

public class Pixel {
    public int r;
    public int g;
    public int b;

    public static Pixel of(int rgb){
        return new Pixel((rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff);
    }

    public Pixel() {
    }

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Pixel(byte r, byte g, byte b) {
        this.r = r & 0xff;
        this.g = g & 0xff;
        this.b = b & 0xff;
    }

    public void set(byte[] rgb) {
        this.r = rgb[0] & 0xff;
        this.g = rgb[1] & 0xff;
        this.b = rgb[2] & 0xff;
    }

    public void set(int[] rgb) {
        this.r = rgb[0];
        this.g = rgb[1];
        this.b = rgb[2];
    }

    public void setR(byte r) {
        this.r = r & 0xff;
    }

    public void setG(byte g) {
        this.g = g & 0xff;
    }

    public void setB(byte b) {
        this.b = b & 0xff;
    }

    public byte getRB() {
        return (byte) r;
    }

    public byte getGB() {
        return (byte) g;
    }

    public byte getBB() {
        return (byte) b;
    }

    public int getRGB() {
        return ((r & 0xff) << 16) + ((g & 0xff) << 8) + ((b & 0xff));
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}
