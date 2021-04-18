package de.zebrajaeger.sphere2cube.pano;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PanoInfo {
    private List<PanoLevel> levels;
    private final int maxLevelIndex;
    private final int tileEdge;

    public PanoInfo(List<PanoLevel> levels, int maxLevelIndex, int tileEdge) {
        this.levels = levels;
        this.maxLevelIndex = maxLevelIndex;
        this.tileEdge = tileEdge;
    }

    public List<PanoLevel> getLevels() {
        return levels;
    }

    public int getMaxLevelIndex() {
        return maxLevelIndex;
    }

    public int getSourceFaceEdge() {
        return levels.stream()
                .filter(l -> l.getLevelIndex() == maxLevelIndex)
                .findFirst()
                .orElseThrow()
                .getFaceEdge();
    }

    public int getTileEdge() {
        return tileEdge;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
