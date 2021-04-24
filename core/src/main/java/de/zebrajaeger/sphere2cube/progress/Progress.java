package de.zebrajaeger.sphere2cube.progress;

public interface Progress {
    Progress DUMMY = new ProgressDummy();

    void start(long total);

    void update(long index);

    void finish();
}
