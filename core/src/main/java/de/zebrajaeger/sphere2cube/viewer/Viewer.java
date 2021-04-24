package de.zebrajaeger.sphere2cube.viewer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.util.Map;

public abstract class Viewer {
    private final Template template;

    public Viewer() {
        template = Mustache.compiler().escapeHTML(false).compile(getTemplate());
    }

    public String render(ViewerConfig config) {
        @SuppressWarnings("unchecked")
        Map<Object, Object> values = new ObjectMapper().convertValue(config, Map.class);
        values.put("css", config.getCssFiles().stream().map(EmbeddedFile::getEmbeddedUrl).toArray());
        values.put("js", config.getJsFiles().stream().map(EmbeddedFile::getEmbeddedUrl).toArray());
        return template.execute(values);
    }

    public abstract String getTemplate();
}
