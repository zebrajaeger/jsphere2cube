package de.zebrajaeger.sphere2cube.viewer;

import de.zebrajaeger.sphere2cube.util.Stringable;

import java.io.File;

public class EmbeddedFile extends Stringable {
    private File source;
    private String embeddedUrl;

    public EmbeddedFile(File source, String embeddedUrl) {
        this.source = source;
        this.embeddedUrl = embeddedUrl;
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public String getEmbeddedUrl() {
        return embeddedUrl;
    }

    public void setEmbeddedUrl(String embeddedUrl) {
        this.embeddedUrl = embeddedUrl;
    }
}
