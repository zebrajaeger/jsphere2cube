package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.zebrajaeger.sphere2cube.Stringable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ViewerConfig{

  @JsonProperty("facebookAppId")
  private String facebookAppId = "966242223397117";
  @JsonProperty("pannellum")
  private ViewerConfigPannellum pannellum = new ViewerConfigPannellum();
  @JsonProperty("marzipano")
  private ViewerConfigMarzipano marzipano = new ViewerConfigMarzipano();

  public boolean isEnabled() {
    return pannellum.isEnabled() || marzipano.isEnabled();
  }
}
