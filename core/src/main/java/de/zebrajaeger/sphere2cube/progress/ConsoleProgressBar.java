package de.zebrajaeger.sphere2cube.progress;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class ConsoleProgressBar implements Progress {
    private ProgressBar progressBar;
    private final String title;
    private final int updateIntervalMs = 200;

    public static ConsoleProgressBar of(String title) {
        return new ConsoleProgressBar(title);
    }

    public ConsoleProgressBar(String title) {
        this.title = title;
    }

    @Override
    public void start(long total) {
        progressBar = new ProgressBar(title,
                total,
                updateIntervalMs,
                System.out,
                ProgressBarStyle.COLORFUL_UNICODE_BLOCK,
                "",
                1,
                false,
                null,
                ChronoUnit.SECONDS,
                0L,
                Duration.ZERO);
    }

    @Override
    public void update(long index) {
        progressBar.stepTo(index);
    }

    @Override
    public void finish() {
        progressBar.stepTo(progressBar.getMax());
        // we want to wait until last update of progressbar happens
        try {
            Thread.sleep(updateIntervalMs+50);
        } catch (InterruptedException e) {
            //ignore
        }
        System.out.println();
    }
}
