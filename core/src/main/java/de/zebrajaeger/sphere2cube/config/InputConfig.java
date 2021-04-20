package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.File;

public class InputConfig {
    @JsonProperty("path")
    private File inputImageFile;
    @JsonProperty("horizontal-angel")
    private Double inputImageHorizontalAngel;
    @JsonProperty("vertical-offset")
    private Double inputImageVerticalOffset;

    public File getInputImageFile() {
        return inputImageFile;
    }

    public void setInputImageFile(File inputImageFile) {
        this.inputImageFile = inputImageFile;
    }

    public Double getInputImageHorizontalAngel() {
        return inputImageHorizontalAngel;
    }

    public Double getInputImageVerticalOffset() {
        return inputImageVerticalOffset;
    }

    public void setInputImageVerticalOffset(Double inputImageVerticalOffset) {
        this.inputImageVerticalOffset = inputImageVerticalOffset;
    }

    public void setInputImageHorizontalAngel(Double inputImageHorizontalAngel) {
        this.inputImageHorizontalAngel = inputImageHorizontalAngel;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
