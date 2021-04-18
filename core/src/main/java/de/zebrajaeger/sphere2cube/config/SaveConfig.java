package de.zebrajaeger.sphere2cube.config;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SaveConfig {

    private boolean saveConfig = false;
    private String saveConfigTarget = "sphere2cube.json";

    public boolean isSaveConfig() {
        return saveConfig;
    }

    public void setSaveConfig(boolean saveConfig) {
        this.saveConfig = saveConfig;
    }

    public String getSaveConfigTarget() {
        return saveConfigTarget;
    }

    public void setSaveConfigTarget(String saveConfigTarget) {
        this.saveConfigTarget = saveConfigTarget;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
