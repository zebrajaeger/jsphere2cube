package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class ViewerConfig {
    @JsonProperty("pannellum")
    private ViewerConfigPannellum pannellum = new ViewerConfigPannellum();

    public ViewerConfigPannellum getPannellum() {
        return pannellum;
    }

    public void setPannellum(ViewerConfigPannellum pannellum) {
        this.pannellum = pannellum;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public void setOutputFolder(File outputFolder) {
        pannellum.setOutputFolder(outputFolder);
    }
}
