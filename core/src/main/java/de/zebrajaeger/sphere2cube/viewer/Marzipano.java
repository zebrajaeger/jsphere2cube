package de.zebrajaeger.sphere2cube.viewer;

import com.drew.lang.Charsets;
import de.zebrajaeger.sphere2cube.config.Config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class Marzipano extends Viewer {

  public Marzipano() {
    super();
  }

  @Override
  public void modifyValues(ViewerConfig config, Map<String, Object> values) {
    values.put("meta", "");
    values.put("html.head.placeholder", "");
  }

  /**
   * <a href="https://www.marzipano.net/docs.html">https://www.marzipano.net/docs.html</a>
   */
  @Override
  public String getTemplate() {
    // {f} : tile face (one of b, d, f, l, r, u)
    // {z} : tile level index (0 is the smallest level)
    // {x} : tile horizontal index
    // {y} : tile vertical index
    try {
      final InputStream is = Objects.requireNonNull(
          Config.class.getResourceAsStream("marzipano.html"));
      return IOUtils.toString(is, Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Could not load marzipano template", e);
    }
  }
}
