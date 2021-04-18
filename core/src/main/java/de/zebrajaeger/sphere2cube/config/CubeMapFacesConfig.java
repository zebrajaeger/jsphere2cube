package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class CubeMapFacesConfig {
    @JsonIgnore
    private File outputFolder;

    @JsonProperty
    private boolean enabled;
    @JsonProperty
    private String target = "{{faceNameLowerCase}}_{{levelCount}}.png";

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
