package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class CubeMapTilesConfig {
    @JsonProperty
    private boolean enabled = true;
    @JsonProperty
    private int tileEdge = 512;
    @JsonProperty
    private String target = "{{levelCount}}/{{faceNameShortLowerCase}}{{xIndex}}_{{yIndex}}.png";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTileEdge() {
        return tileEdge;
    }

    public void setTileEdge(int tileEdge) {
        this.tileEdge = tileEdge;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
