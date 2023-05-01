package de.zebrajaeger.sphere2cube;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.zebrajaeger.sphere2cube.config.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtils {

  public static String toJson(Object source) throws JsonProcessingException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(source);
  }

  public static void saveJson(File targetFile, Object source) throws IOException {
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    ow.writeValue(targetFile, source);
  }

  public static <T> T loadJson(File jsonFile, Class<T> targetClazz, String schema)
      throws IOException {
    validate(jsonFile, schema);
    return loadJson(jsonFile, targetClazz);
  }

  public static <T> T loadJson(String json, Class<T> targetClazz, String schema)
      throws IOException {
    validate(json, schema);
    return loadJson(json, targetClazz);
  }

  public static <T> T loadJson(File jsonFile, Class<T> clazz) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(jsonFile, clazz);
    } catch (JacksonException e) {
      throw new IOException(
          String.format("Failed to read json File '%s'", jsonFile.getAbsolutePath()), e);
    }
  }

  public static <T> T loadJson(String json, Class<T> clazz) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, clazz);
    } catch (JacksonException e) {
      throw new IOException(String.format("Failed to read json String '%s'", json), e);
    }
  }

  /**
   * @see <a href="https://json-schema.org/">Json Schema</a><br>
   * You can try <a href="https://app.quicktype.io/#l=schema">quicktaype</a> to generate a schema
   * from data.
   */
  public static void validate(File file, String schemaName) throws IOException {
    validate(FileUtils.readFileToString(file, StandardCharsets.UTF_8), schemaName);
  }

  public static void validate(String jsonString, String schemaName) {
    try {
      InputStream schemaIs = Objects.requireNonNull(Config.class.getResourceAsStream(schemaName),
          String.format("Schema '%s' not found", schemaName));
      JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaIs));
      Schema schema = SchemaLoader.load(jsonSchema);
      schema.validate(new JSONObject(
          new JSONTokener(IOUtils.toInputStream(jsonString, StandardCharsets.UTF_8))));
    } catch (ValidationException e) {
      System.out.println(e.getMessage());
      e.getCausingExceptions().stream()
          .map(ValidationException::getMessage)
          .forEach(System.out::println);
      throw e;
    }
  }

}
