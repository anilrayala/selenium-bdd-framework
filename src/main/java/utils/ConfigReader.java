package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    public static void loadConfig() {
        prop = new Properties();
        try {
            // ✅ Determine environment (default: qa)
            String env = System.getProperty("env", "qa");
            String configPath = "src/main/resources/config-" + env + ".properties";

            try (FileInputStream fis = new FileInputStream(configPath)) {
                prop.load(fis);
            }

            // ✅ Load environment variables for sensitive data
            String apiKey = System.getenv("API_KEY");
            if (apiKey != null) {
                prop.setProperty("apiKey", apiKey);
            }

            String dbPassword = System.getenv("DB_PASSWORD");
            if (dbPassword != null) {
                prop.setProperty("dbPassword", dbPassword);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config for environment: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        if (prop == null) {
            loadConfig();
        }
        return prop.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
}