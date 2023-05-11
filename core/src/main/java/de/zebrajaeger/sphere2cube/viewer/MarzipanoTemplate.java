package de.zebrajaeger.sphere2cube.viewer;

import java.util.Map;

public class MarzipanoTemplate extends Marzipano {

  public static final String DESCRIPTION_PREVIEW_PATH = "description.preview.path";

  public MarzipanoTemplate() {
    super();
  }

  @Override
  public void modifyValues(ViewerConfig config, Map<String, Object> values) {
    String descriptionPreviewPath = (String) getIfExists(values, DESCRIPTION_PREVIEW_PATH);
    setIfExists(values, DESCRIPTION_PREVIEW_PATH,
        "{{serverUrl}}{{currentPath}}/" + descriptionPreviewPath);
    values.put("meta", "<meta property=\"og:url\" content=\"{{url}}\">");
    values.put("html.head.placeholder", "{{html.head}}");
  }
}

