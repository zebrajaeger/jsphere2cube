package de.zebrajaeger.sphere2cube.multithreading;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobExecutor {
    private final ThreadPoolExecutor executor;

    public JobExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
    }

    public JobExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public JobExecutor(int threads) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    }

    protected void onFinished(Job job) {

    }

    public Future<?> addJob(Job job) {
        job.enqueue(this);
        return executor.submit(job);
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
        boolean ok = executor.awaitTermination(365, TimeUnit.DAYS);
        if (!ok) {
            throw new TimeoutException();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
