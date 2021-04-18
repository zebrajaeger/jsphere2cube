package de.zebrajaeger.sphere2cube.tiles;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TileSaveExecutor {
    private final ThreadPoolExecutor executor;

    public TileSaveExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
    }

    public Future<?> addJob(TileSaveExecutorJob tileSaveExecutorJob) {
        return executor.submit(tileSaveExecutorJob);
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(365, TimeUnit.DAYS);
    }
}
