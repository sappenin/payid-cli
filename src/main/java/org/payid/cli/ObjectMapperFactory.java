package org.payid.cli;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperFactory {

  /**
   * Used for signature generation.
   */
  public static ObjectMapper objectMapperCanonical() {
    return new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Used for display in the terminal.
   */
  public static ObjectMapper objectMapperForDisplay() {
    return new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .enable(SerializationFeature.INDENT_OUTPUT)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static String prettyJson(final String jsonString) {
    try {
      final ObjectMapper objectMapper = objectMapperForDisplay();
      final JsonNode jsonNode = objectMapper.readTree(jsonString);
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }

  }
}
