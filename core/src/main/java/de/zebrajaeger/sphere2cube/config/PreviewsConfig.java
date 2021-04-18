package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class PreviewsConfig {

    @JsonProperty("cubemap")
    private PreviewConfig cubeMapPreview = new PreviewConfig(null, true, 200, "preview_cube.jpg");
    @JsonProperty("equirectangular")
    private PreviewConfig equirectangularPreview = new PreviewConfig(null, true, 200, "preview_equirectangular.jpg");
    @JsonProperty("scaled")
    private PreviewConfig scaledPreview = new PreviewConfig(null, true, 200, "preview_scaled.jpg");

    public PreviewConfig getCubeMapPreview() {
        return cubeMapPreview;
    }

    public void setCubeMapPreview(PreviewConfig cubeMapPreview) {
        this.cubeMapPreview = cubeMapPreview;
    }

    public PreviewConfig getEquirectangularPreview() {
        return equirectangularPreview;
    }

    public void setEquirectangularPreview(PreviewConfig equirectangularPreview) {
        this.equirectangularPreview = equirectangularPreview;
    }

    public PreviewConfig getScaledPreview() {
        return scaledPreview;
    }

    public void setScaledPreview(PreviewConfig scaledPreview) {
        this.scaledPreview = scaledPreview;
    }


    public void setOutputFolder(File outputFolder) {
        getCubeMapPreview().setOutputFolder(outputFolder);
        getScaledPreview().setOutputFolder(outputFolder);
        getEquirectangularPreview().setOutputFolder(outputFolder);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
