package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    public static void loadConfig() {
        prop = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(fis);
        } catch (IOException e) {
            System.out.println("❌ Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException(e);
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
