package de.zebrajaeger.sphere2cube.viewer;

import de.zebrajaeger.sphere2cube.Stringable;
import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewerConfig extends Stringable {
    private List<EmbeddedFile> cssFiles = new ArrayList<>();
    private List<EmbeddedFile> jsFiles = new ArrayList<>();
    private PanoDescription description;

    public ViewerConfig(PanoDescription description) {
        this.description = description;
    }

    public List<EmbeddedFile> getCssFiles() {
        return cssFiles;
    }

    public void setCssFiles(List<EmbeddedFile> cssFiles) {
        this.cssFiles = cssFiles;
    }

    public List<EmbeddedFile> getJsFiles() {
        return jsFiles;
    }

    public void setJsFiles(List<EmbeddedFile> jsFiles) {
        this.jsFiles = jsFiles;
    }

    public PanoDescription getDescription() {
        return description;
    }

    public void setDescription(PanoDescription description) {
        this.description = description;
    }
}
