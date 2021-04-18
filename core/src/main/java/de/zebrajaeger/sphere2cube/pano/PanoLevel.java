package de.zebrajaeger.sphere2cube.pano;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PanoLevel {
    private final int levelIndex;
    private final int tileCount;
    private final int tileEdge;

    public PanoLevel(int levelIndex, int tileCount, int tileEdge) {
        this.levelIndex = levelIndex;
        this.tileCount = tileCount;
        this.tileEdge = tileEdge;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public int getTileCount() {
        return tileCount;
    }

    public int getTileEdge() {
        return tileEdge;
    }

    public int getFaceEdge() {
        return tileCount * tileEdge;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
