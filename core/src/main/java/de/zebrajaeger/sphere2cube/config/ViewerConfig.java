package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ViewerConfig{

  @JsonProperty("facebookAppId")
  private String facebookAppId = "966242223397117";
  @JsonProperty("pannellum")
  private ViewerConfigPannellum pannellum = new ViewerConfigPannellum();
  @JsonProperty("marzipano")
  private ViewerConfigMarzipano marzipano = new ViewerConfigMarzipano();

  @JsonIgnore
  public boolean isEnabled() {
    return pannellum.isEnabled() || marzipano.isEnabled();
  }
}
