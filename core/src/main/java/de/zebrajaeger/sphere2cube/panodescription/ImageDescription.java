package de.zebrajaeger.sphere2cube.panodescription;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;

public class ImageDescription extends Stringable {
    @JsonProperty
    private String path;
    @JsonProperty
    private String alt;

    public ImageDescription() {
    }

    public ImageDescription(String path, String alt) {
        this.path = path;
        this.alt = alt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

}
