package de.zebrajaeger.sphere2cube.progress;

public interface Progress {
    public static final Progress DUMMY = new ProgressDummy();

    void start(long total);

    void update(long index);

    void finish();
}
