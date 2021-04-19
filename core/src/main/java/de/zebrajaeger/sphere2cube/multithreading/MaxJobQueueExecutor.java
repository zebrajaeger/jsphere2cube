package de.zebrajaeger.sphere2cube.multithreading;

import java.util.concurrent.Future;

public class MaxJobQueueExecutor extends JobExecutor {
    private final int maxJobCount;
    private int runningJobs = 0;
    private final Object lock = new Object();

    public static MaxJobQueueExecutor withMaxQueueSize(int queuedJobCount){
        int cores = Runtime.getRuntime().availableProcessors();
        return new MaxJobQueueExecutor(cores + queuedJobCount);
    }

    public MaxJobQueueExecutor(int maxJobCount) {
        super();
        this.maxJobCount = maxJobCount;
    }

    public MaxJobQueueExecutor(int threadCount, int maxJobCount) {
        super(threadCount);
        this.maxJobCount = maxJobCount;
    }

    public Future<?> addJob(Job job) {
        synchronized (lock) {
            while (runningJobs >= maxJobCount) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Future<?> future = super.addJob(job);
            runningJobs++;
            return future;
        }
    }

    @Override
    protected void onFinished(Job job) {
        super.onFinished(job);
        synchronized (lock) {
            runningJobs--;
            lock.notify();
        }
    }
}
