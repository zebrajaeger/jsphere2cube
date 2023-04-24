package de.zebrajaeger.sphere2cube.viewer;

import de.zebrajaeger.sphere2cube.panodescription.PanoDescription;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public abstract class ViewerConfig {

  private List<EmbeddedFile> cssFiles = new ArrayList<>();
  private List<EmbeddedFile> jsFiles = new ArrayList<>();
  private PanoDescription description;

  private String facebookAppId;
  private boolean isTemplate;

  public ViewerConfig(PanoDescription description) {
    this.description = description;
  }
}
