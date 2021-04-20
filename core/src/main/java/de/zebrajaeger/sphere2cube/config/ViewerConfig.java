package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ViewerConfig {
    @JsonProperty("pannellum")
    private ViewerConfigPannellum pannellum = new ViewerConfigPannellum();
    private ViewerConfigMarzipano marzipano = new ViewerConfigMarzipano();

    public ViewerConfigPannellum getPannellum() {
        return pannellum;
    }

    public void setPannellum(ViewerConfigPannellum pannellum) {
        this.pannellum = pannellum;
    }

    public ViewerConfigMarzipano getMarzipano() {
        return marzipano;
    }

    public void setMarzipano(ViewerConfigMarzipano marzipano) {
        this.marzipano = marzipano;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
