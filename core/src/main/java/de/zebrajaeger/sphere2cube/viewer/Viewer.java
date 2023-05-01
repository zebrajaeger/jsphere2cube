package de.zebrajaeger.sphere2cube.viewer;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.util.Map;

public abstract class Viewer {

  private final Template template;

  public Viewer() {
    template = Mustache.compiler().escapeHTML(false).compile(getTemplate());
  }

  public String render(ViewerConfig config) {
    Map<String, Object> values = new ObjectMapper().map(config);
    values.put("css", config.getCssFiles().stream().map(EmbeddedFile::getEmbeddedUrl).toArray());
    values.put("js", config.getJsFiles().stream().map(EmbeddedFile::getEmbeddedUrl).toArray());
    modifyValues(config, values);
    return template.execute(values);
  }

  public abstract String getTemplate();

  public void modifyValues(@SuppressWarnings("unused")ViewerConfig config, @SuppressWarnings("unused") Map<String, Object> values) {
  }

  public Object getIfExists(Map<String, Object> toSet, String key) {
    Map<String, Object> current = toSet;
    final String[] parts = key.split("\\.");
    for (int i = 0; current != null && i < parts.length; ++i) {
      if (i < parts.length - 1) {
        //noinspection unchecked
        current = (Map<String, Object>) current.get(parts[i]);
      } else {
        return current.get(parts[i]);
      }
    }
    return null;
  }

  public void setIfExists(Map<String, Object> toSet, String key, String value) {
    Map<String, Object> current = toSet;
    final String[] parts = key.split("\\.");
    for (int i = 0; current != null && i < parts.length; ++i) {
      if (i < parts.length - 1) {
        //noinspection unchecked
        current = (Map<String, Object>) current.get(parts[i]);
      } else {
        current.put(parts[i], value);
      }
    }
  }
}
