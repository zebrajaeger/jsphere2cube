package de.zebrajaeger.sphere2cube;

import de.zebrajaeger.sphere2cube.config.Config;
import de.zebrajaeger.sphere2cube.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.everit.json.schema.ValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchemaTest {

    private String loadResourceAsString(String name) {
        try (InputStream is = Objects.requireNonNull(Config.class.getResourceAsStream(name), "json not found " + name)) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("resource not found", e);
        }
    }

    @Test
    public void test1() {
        String config = loadResourceAsString("config.2.json");
        Exception exception = assertThrows(ValidationException.class, () -> JsonUtils.validate(config, Config.SPHERE_2_CUBE_SCHEMA_JSON));

        assertEquals("#/debug: expected type: Boolean, found: String", exception.getMessage());
    }
}
