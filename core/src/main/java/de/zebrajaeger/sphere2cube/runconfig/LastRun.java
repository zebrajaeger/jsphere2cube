package de.zebrajaeger.sphere2cube.runconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.Stringable;
import de.zebrajaeger.sphere2cube.TimeUtils;

public class LastRun extends Stringable {
    @JsonProperty
    private String configHash;
    @JsonProperty
    private Long timestamp;
    @JsonProperty
    private String result;
    @JsonProperty
    private Long runtimeMs;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getHumanTime() {
        return TimeUtils.toIsoTime(timestamp);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getHumanRunTime() {
        return TimeUtils.durationToHumanString(runtimeMs);
    }

    public String getConfigHash() {
        return configHash;
    }

    public void setConfigHash(String configHash) {
        this.configHash = configHash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getRuntimeMs() {
        return runtimeMs;
    }

    public void setRuntimeMs(Long runtimeMs) {
        this.runtimeMs = runtimeMs;
    }
}
