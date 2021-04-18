package de.zebrajaeger.sphere2cube;

public enum Face {
    BACK(Pixel.of(0xff0000)), LEFT(Pixel.of(0xff00)), FRONT(Pixel.of(0xff)), RIGHT(Pixel.of(0xff00ff)), UP(Pixel.of(0xffff00)), DOWN(Pixel.of(0xffff));
    private Pixel color;

    Face(Pixel color) {
        this.color = color;
    }

    public Pixel getColor() {
        return color;
    }
}
