package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArchiveConfig {
    @JsonProperty
    private boolean enabled = true;
    @JsonProperty
    private String target = "pano.zip";

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
}
