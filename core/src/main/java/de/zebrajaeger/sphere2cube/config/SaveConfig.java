package de.zebrajaeger.sphere2cube.config;

import de.zebrajaeger.sphere2cube.Stringable;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SaveConfig extends Stringable {

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

}
