package de.zebrajaeger.sphere2cube;

import org.apache.commons.lang3.time.StopWatch;

public class Chronograph {
    private final StopWatch stopWatch;

    public static Chronograph start() {
        return new Chronograph();
    }

    public Chronograph() {
        stopWatch = new StopWatch();
        stopWatch.start();
    }

    public String stop() {
        stopWatch.stop();
        return TimeUtils.durationToHumanString(stopWatch.getTime());
    }


}
