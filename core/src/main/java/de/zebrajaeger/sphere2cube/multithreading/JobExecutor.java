package de.zebrajaeger.sphere2cube.multithreading;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobExecutor {
    private final ThreadPoolExecutor executor;

    public static ThreadPoolExecutor createDefaultExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
    }

    public JobExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public JobExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = createDefaultExecutor();
    }

    public Future<?> addJob(Job job) {
        return executor.submit(job);
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        boolean ok = executor.awaitTermination(365, TimeUnit.DAYS);
        if(!ok){
            throw new TimeoutException();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
