package de.zebrajaeger.sphere2cube.pano;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PanoInfo {
    private List<PanoLevel> levels;
    private final int maxLevel;
    private final int tileEdge;

    public PanoInfo(List<PanoLevel> levels, int maxLevel, int tileEdge) {
        this.levels = levels;
        this.maxLevel = maxLevel;
        this.tileEdge = tileEdge;
    }


    public List<PanoLevel> getLevels() {
        return levels;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getTileEdge() {
        return tileEdge;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
