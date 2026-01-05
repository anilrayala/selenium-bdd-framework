package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

/**
 * Stateless JSON reader utility.
 * Reads a JSON file and returns data as Map<String, String>.
 */
public final class JsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonReader() {}

    public static Map<String, Map<String, String>> readJsonAsMap(
            String filePath
    ) {
        try {
            return MAPPER.readValue(
                    new File(filePath),
                    new TypeReference<Map<String, Map<String, String>>>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to read JSON file: " + filePath, e
            );
        }
    }
}
