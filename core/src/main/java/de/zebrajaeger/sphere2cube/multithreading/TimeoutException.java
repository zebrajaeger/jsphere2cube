package de.zebrajaeger.sphere2cube.multithreading;

public class TimeoutException extends InterruptedException {
    public TimeoutException() {
    }

    public TimeoutException(String s) {
        super(s);
    }
}
