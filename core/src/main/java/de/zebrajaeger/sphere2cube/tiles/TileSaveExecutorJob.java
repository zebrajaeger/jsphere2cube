package de.zebrajaeger.sphere2cube.tiles;

import de.zebrajaeger.sphere2cube.Img;
import de.zebrajaeger.sphere2cube.ImgUtils;
import de.zebrajaeger.sphere2cube.Pixel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TileSaveExecutorJob implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(TileSaveExecutorJob.class);
    private final Img sourceImage;
    private final File targetFile;
    private final int targetEdge;
    private final int xOffset;
    private final int yOffset;
    private final boolean debug;

    public TileSaveExecutorJob(Img sourceImage, File targetFile, int targetEdge, int xOffset, int yOffset, boolean debug) {
        this.sourceImage = sourceImage;
        this.targetFile = targetFile;
        this.targetEdge = targetEdge;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.debug = debug;
    }

    @Override
    public void run() {
        Img tile = Img.rectangular(targetEdge);

        sourceImage.copyTo(tile, xOffset, yOffset);
        try {
            FileUtils.forceMkdirParent(targetFile);
            if (debug) {
                ImgUtils.drawDottedBorder(tile, Pixel.of(0xffffff));
            }
            LOG.info("Save tile: {}", targetFile.getAbsolutePath());

            ImgUtils.save(tile, targetFile, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
