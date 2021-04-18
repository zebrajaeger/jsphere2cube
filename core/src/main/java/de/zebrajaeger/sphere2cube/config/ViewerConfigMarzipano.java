package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class ViewerConfigMarzipano {
    @JsonIgnore
    private File outputFolder;

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
