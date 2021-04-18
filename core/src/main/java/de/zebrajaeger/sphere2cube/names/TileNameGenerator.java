package de.zebrajaeger.sphere2cube.names;

import de.zebrajaeger.sphere2cube.Face;
import de.zebrajaeger.sphere2cube.pano.PanoInfo;

import java.util.HashMap;
import java.util.Map;

public class TileNameGenerator extends NameGenerator {

    public TileNameGenerator(String templateString) {
        super(templateString);
    }

    public String generate(PanoInfo panoInfo, int levelIndex, Face face, int xIndex, int yIndex) {

        Map<String, Object> model = new HashMap<>();

        model.put("levelIndex", levelIndex);
        model.put("levelCount", levelIndex + 1);
        model.put("inverseLevelIndex", panoInfo.getMaxLevelIndex() - levelIndex);
        model.put("inverseLevelCount", panoInfo.getMaxLevelIndex() - levelIndex + 1);

        model.put("faceNameUpperCase", face.name().toUpperCase());
        model.put("faceNameLowerCase", face.name().toLowerCase());
        model.put("faceNameShortUpperCase", face.name().substring(0, 1).toUpperCase());
        model.put("faceNameShortLowerCase", face.name().substring(0, 1).toLowerCase());

        model.put("xIndex", xIndex);
        model.put("xCount", xIndex + 1);
        model.put("yIndex", yIndex);
        model.put("yCount", yIndex + 1);

        return render(model);
    }
}
