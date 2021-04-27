package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.Stringable;

public class ViewerConfig extends Stringable {
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

    public boolean isEnabled() {
        return pannellum.isEnabled() || marzipano.isEnabled();
    }

}
