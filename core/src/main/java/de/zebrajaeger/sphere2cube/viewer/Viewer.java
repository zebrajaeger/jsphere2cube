package de.zebrajaeger.sphere2cube.viewer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.util.Map;

public abstract class Viewer {
    private final Template template;

    public interface Config {

    }

    public Viewer() {
        template = Mustache.compiler().escapeHTML(false).compile(getTemplate());
    }

    public abstract String getTemplate();

    public String render(Config config) {
        return template.execute(new ObjectMapper().convertValue(config, Map.class));
    }
}
