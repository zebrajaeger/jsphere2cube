package de.zebrajaeger.sphere2cube.viewer;

public class PanellumConfig extends ViewerConfig {
    private String htmlTitle = "sphere2cube-java with Pannellum";
    private int levelCount;
    private int targetImageSize;
    private int tileSize;
    private String tileFileType = "png";
    private String previewPath = "preview_scaled.jpg";
    private boolean autoLoad = true;
    private double xMin = -180d;
    private double xMax = 180d;
    private double yMin = -90d;
    private double yMax = 90d;

    public PanellumConfig(int levelCount, int targetImageSize, int tileSize) {
        this.levelCount = levelCount;
        this.targetImageSize = targetImageSize;
        this.tileSize = tileSize;
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    public int getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(int levelCount) {
        this.levelCount = levelCount;
    }

    public int getTargetImageSize() {
        return targetImageSize;
    }

    public void setTargetImageSize(int targetImageSize) {
        this.targetImageSize = targetImageSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public String getTileFileType() {
        return tileFileType;
    }

    public void setTileFileType(String tileFileType) {
        this.tileFileType = tileFileType;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public double getXMin() {
        return xMin;
    }

    public void setXMin(double xMin) {
        this.xMin = xMin;
    }

    public double getXMax() {
        return xMax;
    }

    public void setXMax(double xMax) {
        this.xMax = xMax;
    }

    public double getYMin() {
        return yMin;
    }

    public void setYMin(double yMin) {
        this.yMin = yMin;
    }

    public double getYMax() {
        return yMax;
    }

    public void setYMax(double yMax) {
        this.yMax = yMax;
    }
}
