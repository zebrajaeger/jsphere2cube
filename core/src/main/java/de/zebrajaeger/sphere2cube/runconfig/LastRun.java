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
    private PanoProcessState lastRun;

    public static RunConfig of(File configFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(configFile, RunConfig.class);
    }

    public String getConfigHash() {
        return configHash;
    }

    public void setConfigHash(String configHash) {
        this.configHash = configHash;
    }

    public PanoProcessState getLastRun() {
        return lastRun;
    }

    public void setLastRun(PanoProcessState lastRun) {
        this.lastRun = lastRun;
    }
}
