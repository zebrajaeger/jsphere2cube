package de.zebrajaeger.sphere2cube.viewer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;
import de.zebrajaeger.sphere2cube.pano.PanoLevel;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class MarzipanoConfig implements Viewer.Config {
    private String htmlTitle = "sphere2cube-java with Marzipano";

    private String levels;
    private String tileFileType = "png";
    private String previewPath = "preview_scaled.jpg";

    class Level {
        @JsonProperty
        private final int tileSize;
        @JsonProperty
        private final int size;

        public Level(int tileSize, int size) {
            this.tileSize = tileSize;
            this.size = size;
        }

        public int getTileSize() {
            return tileSize;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }

    public MarzipanoConfig(PanoInfo panoInfo) {
        List<Level> temp = new ArrayList<>();
        for (PanoLevel l : panoInfo.getLevels()) {
            temp.add(new Level(l.getTileEdge(), l.getFaceEdge()));
        }

        try {
            this.levels = new ObjectMapper().writer().writeValueAsString(temp);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Config", e);
        }
    }

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }

    public String getLevels() {
        return levels;
    }

    public void setLevels(String levels) {
        this.levels = levels;
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

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
