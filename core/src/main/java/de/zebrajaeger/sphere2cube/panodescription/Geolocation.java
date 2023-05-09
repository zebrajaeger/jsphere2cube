package de.zebrajaeger.sphere2cube.panodescription;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;

public class Geolocation extends Stringable {
    @JsonProperty
    private Double latitude;
    @JsonProperty
    private Double longitude;

    public Geolocation() {
    }

    public Geolocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
