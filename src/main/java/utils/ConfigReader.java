package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * ConfigReader - loads environment-specific configuration using the following precedence:
 *
 * 1. Java System Property      (-Dkey=value)
 * 2. Environment Variable      (API_KEY, HEADLESS, PAGE_LOAD_TIMEOUT)
 * 3. config-<env>.properties   (file-based defaults)
 *
 * Environment is selected using:  -Denv=dev|qa|prod
 * Default environment = "qa"
 */
public class ConfigReader {

    private static volatile Properties prop;

    /**
     * Loads config-<env>.properties file based on -Denv system property.
     * This method is idempotent: it loads the file only once.
     */
    public static synchronized void loadConfig() {
        if (prop != null) {
            return; // already loaded
        }

        prop = new Properties();

        // Determine environment name (default: qa)
        String env = System.getProperty("env", "qa").trim();
        String configPath = "src/main/resources/config-" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(configPath)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config file: " + configPath + " (env=" + env + "): " + e.getMessage(), e);
        }

        // Overlay well-known sensitive environment variables if present
        overlayEnvSecret("apiKey");
        overlayEnvSecret("dbPassword");
    }

    // Overlay a secret key from environment variables (API_KEY, DB_PASSWORD, etc.)
    private static void overlayEnvSecret(String key) {
        String val = lookupEnvVariants(key);
        if (val != null) {
            prop.setProperty(key, val);
        }
    }

    /**
     * Get property value with precedence:
     *
     * 1) System.getProperty(key)
     * 2) Environment variables ("KEY", "KEY_NAME", camelCase → UPPER_SNAKE)
     * 3) config-<env>.properties file
     */
    public static String getProperty(String key) {
        if (prop == null) {
            loadConfig();
        }

        // 1) Highest precedence: SYSTEM PROPERTY (-Dkey=value)
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }

        // 2) Environment variables
        String envVal = lookupEnvVariants(key);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }

        // 3) config-<env>.properties
        return prop.getProperty(key);
    }

    /**
     * Try common environment variable variants:
     *  key            -> System.getenv("key")
     *  KEY            -> System.getenv("KEY")
     *  keyName        -> System.getenv("KEY_NAME")
     *  key_name       -> System.getenv("KEY_NAME")
     */
    private static String lookupEnvVariants(String key) {
        // exact key
        String val = System.getenv(key);
        if (val != null) return val;

        // uppercase
        val = System.getenv(key.toUpperCase(Locale.ROOT));
        if (val != null) return val;

        // camelCase -> KEY_NAME
        String upperSnake = toUpperSnake(key);
        val = System.getenv(upperSnake);
        if (val != null) return val;

        // lowercase fallback
        val = System.getenv(key.toLowerCase(Locale.ROOT));
        if (val != null) return val;

        return null;
    }

    private static String toUpperSnake(String key) {
        StringBuilder sb = new StringBuilder();
        char[] chars = key.replace('.', '_').toCharArray();

        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append("_").append(c);
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString().replaceAll("__+", "_").replaceFirst("^_", "");
    }

    public static int getInt(String key) {
        String val = getProperty(key);
        if (val == null) {
            throw new RuntimeException("Missing integer property for key: " + key);
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid integer for key '" + key + "' value: " + val, e);
        }
    }

    public static boolean getBoolean(String key) {
        String val = getProperty(key);
        return val != null && Boolean.parseBoolean(val.trim());
    }
}
