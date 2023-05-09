package de.zebrajaeger.sphere2cube.panodescription;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PanoDescription extends Stringable {
    @JsonProperty
    private String title;
    @JsonProperty
    private String description;
    @JsonProperty
    private Geolocation location;
    @JsonProperty
    private List<String> tags;

    @JsonIgnore
    private String type = "website";
    @JsonIgnore
    private ImageDescription preview = new ImageDescription("preview_scaled.jpg", "Panorama preview");
}

