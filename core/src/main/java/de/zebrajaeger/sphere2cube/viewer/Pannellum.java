package de.zebrajaeger.sphere2cube.viewer;

import com.drew.lang.Charsets;
import de.zebrajaeger.sphere2cube.config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class Pannellum extends Viewer {

  public Pannellum() {
    super();
  }

  @Override
  public void modifyValues(ViewerConfig config, Map<String, Object> values) {
    values.put("meta", "");
    values.put("html.head.placeholder", "");
  }

  @Override
  public String getTemplate() {
    try {
      final InputStream is = Objects.requireNonNull(
          Config.class.getResourceAsStream("pannellum.html"));
      return IOUtils.toString(is, Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Could not load pannellum template", e);
    }
  }
}
