package de.zebrajaeger.sphere2cube.runconfig;

import java.util.LinkedList;
import java.util.List;

class CollectingCallback implements Callback {
    private List<PanoDirectory> panoDirectories = new LinkedList<>();

    @Override
    public void onPano(PanoDirectory panoDirectory) {
        if (panoDirectory.isPanoDir()) {
            panoDirectories.add(panoDirectory);
        }
    }

    public List<PanoDirectory> getPanoDirectories() {
        return panoDirectories;
    }
}
