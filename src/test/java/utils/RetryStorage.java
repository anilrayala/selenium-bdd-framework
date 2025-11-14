package utils;

import java.nio.file.Path;

/**
 * RetryStorage - stores a per-thread temporary screenshot path (to be used by TestRetryAnalyzer).
 * Uses ThreadLocal so it's safe for parallel execution (assuming one scenario per thread).
 */
public class RetryStorage {

    private static final ThreadLocal<Path> lastFailureScreenshotPath = new ThreadLocal<>();
    private static final ThreadLocal<String> lastFailureMessage = new ThreadLocal<>();

    public static void setScreenshotPath(Path p) {
        lastFailureScreenshotPath.set(p);
    }

    public static Path getScreenshotPath() {
        return lastFailureScreenshotPath.get();
    }

    public static void clearScreenshotPath() {
        lastFailureScreenshotPath.remove();
    }

    public static void setLastFailureMessage(String msg) {
        lastFailureMessage.set(msg);
    }

    public static String getLastFailureMessage() {
        return lastFailureMessage.get();
    }

    public static void clearAll() {
        lastFailureScreenshotPath.remove();
        lastFailureMessage.remove();
    }
}
