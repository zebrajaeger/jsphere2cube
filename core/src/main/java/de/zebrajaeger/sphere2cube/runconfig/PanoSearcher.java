package de.zebrajaeger.sphere2cube.runconfig;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class PanoSearcher implements Callable<List<PanoDirectory>> {
    private static final Logger LOG = LoggerFactory.getLogger(PanoSearcher.class);
    private File scanRoot;

    private PanoSearcher() {
    }

    public PanoSearcher(File scanRoot) {
        this.scanRoot = scanRoot;
    }

    public static List<PanoDirectory> scanRecursive(File root) throws IOException {
        return new PanoSearcher().scanRecursiveIntern(root);
    }

    public static void scanRecursive(File root, Callback callback) throws IOException {
        new PanoSearcher().scanRecursiveIntern(root, callback);
    }

    private List<PanoDirectory> scanRecursiveIntern(File root) throws IOException {
        CollectingCallback callback = new CollectingCallback();
        scanRecursiveIntern(root, callback);
        return callback.getPanoDirectories();
    }

    private void scanRecursiveIntern(File root, Callback callback) throws IOException {
        LOG.debug("scan '{}'", root.getAbsolutePath());
        File[] rootFiles = root.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
        if (rootFiles == null) {
            return;
        }

        for (File f : rootFiles) {
            PanoDirectory panoDirectory = PanoDirectory.of(f);
            if (panoDirectory.isPanoDir()) {
                callback.onPano(panoDirectory);
            } else {
                // do recursion
                scanRecursiveIntern(f, callback);
            }
        }
    }

    @Override
    public List<PanoDirectory> call() throws Exception {
        return scanRecursive(scanRoot);
    }
}
