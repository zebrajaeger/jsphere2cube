package de.zebrajaeger.sphere2cube.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

@Data
public class ViewerConfigBase {

  @JsonProperty
  private boolean enabled = true;

  @JsonProperty
  @NonNull
  private String target;

  @JsonProperty
  @NonNull
  private String templateTarget;

  @JsonProperty("css")
  private List<URL> cssFiles = new ArrayList<>();

  @JsonProperty("js")
  private List<URL> jsFiles = new ArrayList<>();
}
