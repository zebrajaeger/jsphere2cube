package de.zebrajaeger.sphere2cube.multithreading;

public abstract class Job implements Runnable {
    private JobExecutor jobExecutor;

    protected void enqueue(JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    @Override
    public final void run() {
        try {
            exec();
        } finally {
            jobExecutor.onFinished(this);
        }
    }

    abstract public void exec();
}
