package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class PreviewConfig {
    @JsonIgnore
    private File outputFolder;

    @JsonProperty
    private boolean enabled;
    @JsonProperty
    private int edge;
    @JsonProperty
    private String target;

    public PreviewConfig() {
    }

    public PreviewConfig(File outputFolder, boolean enabled, int edge, String target) {
        this.outputFolder = outputFolder;
        this.enabled = enabled;
        this.edge = edge;
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

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
