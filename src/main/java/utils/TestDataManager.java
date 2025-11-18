package utils;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TestDataManager - Reads test data from JSON file.
 * Usage:
 * JSONObject data = TestDataManager.getData("login");
 * String username = data.getString("username");
 */
public class TestDataManager {

    private static final String DATA_FILE = "src/test/resources/testdata.json";

    public static JSONObject getData(String key) {
        try {
            String content = Files.readString(Path.of(DATA_FILE));
            JSONObject json = new JSONObject(content);
            return json.getJSONObject(key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read test data for key: " + key, e);
        }
    }
}