package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class CubeMapConfig {
    @JsonProperty("tiles")
    private CubeMapTilesConfig tiles = new CubeMapTilesConfig();
    @JsonProperty("faces")
    private CubeMapFacesConfig faces = new CubeMapFacesConfig();

    public CubeMapTilesConfig getTiles() {
        return tiles;
    }

    public void setTiles(CubeMapTilesConfig tiles) {
        this.tiles = tiles;
    }

    public CubeMapFacesConfig getFaces() {
        return faces;
    }

    public void setFaces(CubeMapFacesConfig faces) {
        this.faces = faces;
    }

    public void setOutputFolder(File outputFolder) {
        tiles.setOutputFolder(outputFolder);
        faces.setOutputFolder(outputFolder);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
