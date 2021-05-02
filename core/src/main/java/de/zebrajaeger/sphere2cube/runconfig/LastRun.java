package de.zebrajaeger.sphere2cube.runconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.sphere2cube.PanoProcessState;
import de.zebrajaeger.sphere2cube.Stringable;

import java.io.File;
import java.io.IOException;

public class LastRun extends Stringable {
    @JsonProperty
    private String configHash;
    @JsonProperty
    private PanoProcessState panoProcessState;

    public static LastRun of(File configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(configFile, LastRun.class);
    }

    public String getConfigHash() {
        return configHash;
    }

    public void setConfigHash(String configHash) {
        this.configHash = configHash;
    }

    public PanoProcessState getPanoProcessState() {
        return panoProcessState;
    }

    public void setPanoProcessState(PanoProcessState panoProcessState) {
        this.panoProcessState = panoProcessState;
    }
}
