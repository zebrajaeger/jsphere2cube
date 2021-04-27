package de.zebrajaeger.sphere2cube.runconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zebrajaeger.sphere2cube.Stringable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RunConfig extends Stringable {
    @JsonProperty
    private String configHash;
    @JsonProperty
    private List<LastRun> lastRuns;

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

    public List<LastRun> getLastRuns() {
        return lastRuns;
    }

    public void setLastRuns(List<LastRun> lastRuns) {
        this.lastRuns = lastRuns;
    }
}
