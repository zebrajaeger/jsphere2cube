package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;

public class PreviewConfig extends Stringable {
    @JsonProperty
    private boolean enabled;
    @JsonProperty
    private int edge;
    @JsonProperty
    private Integer maxSize;
    @JsonProperty
    private String target;

    public PreviewConfig() {
    }

    public PreviewConfig(boolean enabled, int edge, String target) {
        this.enabled = enabled;
        this.edge = edge;
        this.target = target;
    }

    public PreviewConfig(boolean enabled, int edge, Integer maxSize, String target) {
        this.enabled = enabled;
        this.edge = edge;
        this.maxSize = maxSize;
        this.target = target;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getEdge() {
        return edge;
    }

    public void setEdge(int edge) {
        this.edge = edge;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}
