package de.zebrajaeger.sphere2cube.panodescription;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.Stringable;

public class PanoDescription extends Stringable {
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private String type = "website";
    @JsonProperty
    private ImageDescription preview = new ImageDescription("preview_scaled.jpg", "Panorama preview");
    @JsonProperty
    private Geolocation location;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageDescription getPreview() {
        return preview;
    }

    public void setPreview(ImageDescription preview) {
        this.preview = preview;
    }

    public Geolocation getLocation() {
        return location;
    }

    public void setLocation(Geolocation location) {
        this.location = location;
    }
}

