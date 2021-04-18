package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class InputConfig {
    @JsonProperty("path")
    private File inputImageFile;
    @JsonProperty("angel")
    private double inputImageHorizontalAngel = 360d;

    public File getInputImageFile() {
        return inputImageFile;
    }

    public void setInputImageFile(File inputImageFile) {
        this.inputImageFile = inputImageFile;
    }

    public double getInputImageHorizontalAngel() {
        return inputImageHorizontalAngel;
    }

    public void setInputImageHorizontalAngel(double inputImageHorizontalAngel) {
        this.inputImageHorizontalAngel = inputImageHorizontalAngel;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
