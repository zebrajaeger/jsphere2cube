package de.zebrajaeger.sphere2cube.zip;

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;

public class Zipper {
    private static final Logger LOG = LoggerFactory.getLogger(Zipper.class);

    public static void compress(File sourceFolder, File zipFile) throws IOException, ExecutionException, InterruptedException {
        ParallelScatterZipCreator scatterZipCreator = new ParallelScatterZipCreator();

        assert sourceFolder.isDirectory();
        int srcFolderLength = sourceFolder.getAbsolutePath().length() + 1;  // +1 to remove the last file separator

        if (zipFile.exists()) {
            if (zipFile.delete()) {
                LOG.info("Old zip file '{}' deleted'", zipFile.getAbsolutePath());
            }
        }

        try (ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(new FileOutputStream(zipFile))) {
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);

            Iterator<File> fileIterator = FileUtils.iterateFiles(sourceFolder, null, true);
            fileIterator.forEachRemaining(file -> {
                // ignore zip file itself
                if (!file.equals(zipFile)) {
                    String relativePath = file.getAbsolutePath().substring(srcFolderLength);
                    InputStreamSupplier streamSupplier = () -> {
                        try {
                            return Files.newInputStream(file.toPath());
                        } catch (IOException e) {
                            LOG.error("Zip Entry", e);
                        }
                        return null;
                    };
                    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(relativePath);
                    zipArchiveEntry.setMethod(ZipEntry.DEFLATED);
                    scatterZipCreator.addArchiveEntry(zipArchiveEntry, streamSupplier);
                }
            });

            scatterZipCreator.writeTo(zipArchiveOutputStream);
        }
    }
}
