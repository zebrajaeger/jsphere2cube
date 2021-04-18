package de.zebrajaeger.sphere2cube.pano;

import de.zebrajaeger.sphere2cube.EquirectangularImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PanoUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PanoUtils.class);

    public static PanoInfo calcPanoInfo(EquirectangularImage source, int tileEdge) {
        List<PanoLevel> levels = new ArrayList<>();
        // TODO add log
        int sourceEdge = source.getWidth() / 4;
        int levelIndex = 0;
        int tileCount = 1;

        int levelEdge = tileEdge;
        while (sourceEdge > levelEdge) {
            levels.add(new PanoLevel(levelIndex, tileCount, tileEdge));
            levelIndex++;
            tileCount *= 2;
            levelEdge *= 2;
        }
        levels.add(new PanoLevel(levelIndex, tileCount, tileEdge));

        return new PanoInfo(levels, levelIndex, tileEdge);
    }
}
