package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Map;

/**
 * Stateless JSON reader utility.
 * Reads JSON from classpath and returns Map<String, String>.
 */
public final class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonReader() {}

    public static Map<String, Map<String, String>> readJsonAsMap(
            String resourcePath
    ) {
        try (InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "JSON file not found on classpath: " + resourcePath
                );
            }

            return MAPPER.readValue(
                    is,
                    new TypeReference<Map<String, Map<String, String>>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read JSON from classpath: " + resourcePath, e
            );
        }
    }
}
