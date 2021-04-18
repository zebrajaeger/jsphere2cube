package de.zebrajaeger.sphere2cube.names;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.util.Map;

public abstract class NameGenerator {
    private final Template template;

    public NameGenerator(String templateString) {
        template = Mustache.compiler().compile(templateString);
    }

    protected String render(Map<String, Object> model) {
        return template.execute(model);
    }
}
