package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.util.Stringable;

public class InputConfig extends Stringable {
    @JsonProperty("path")
    private String inputImageFile;
    @JsonProperty("horizontal-angel")
    private Double inputImageHorizontalAngel;
    @JsonProperty("vertical-offset")
    private Double inputImageVerticalOffset;

    public String getInputImageFile() {
        return inputImageFile;
    }

    public void setInputImageFile(String inputImageFile) {
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

}
