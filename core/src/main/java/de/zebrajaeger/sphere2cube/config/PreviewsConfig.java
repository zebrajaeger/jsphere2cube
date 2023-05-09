package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;

public class PreviewsConfig extends Stringable {
    @JsonProperty("cubemap")
    private PreviewConfig cubeMapPreview = new PreviewConfig(true, 200, "preview_cube.jpg");
    @JsonProperty("equirectangular")
    private PreviewConfig equirectangularPreview = new PreviewConfig(true, 200, "preview_equirectangular.jpg");
    @JsonProperty("scaled")
    private PreviewConfig scaledPreview = new PreviewConfig(true, 200, 300000,"preview_scaled.jpg");

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

}
