package utils;

import factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.net.UnknownHostException;

/**
 * TestRetryAnalyzer - retries failed tests. Preferred screenshot capture:
 * 1) If driver exists at retry time -> capture live screenshot and attach via ExtentTestManager.
 * 2) If driver is null -> attempt to read temp screenshot file path from RetryStorage and attach its contents.
 *
 * Retry count is read in this order:
 * - System property -DretryCount (CLI)
 * - ConfigReader.getInt("retryCount")
 *
 * Added behavior:
 * - Only retries when the failure is considered "transient" (timeouts, session issues, stale elements,
 *   network problems, certain WebDriver exceptions).
 * - Does NOT retry on assertion failures or explicit test skips.
 * - Logs attempt number and exception details to Extent on each retry.
 */
public class TestRetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(TestRetryAnalyzer.class);
    private final int maxRetry;
    private int attempt = 0;

    public TestRetryAnalyzer() {
        int configured = 0;
        try {
            String sys = System.getProperty("retryCount");
            if (sys != null && !sys.isBlank()) {
                configured = Integer.parseInt(sys);
            } else {
                configured = ConfigReader.getInt("retryCount");
            }
        } catch (Exception e) {
            configured = 0;
            logger.warn("retryCount missing/invalid; defaulting to 0 (no retries). Reason: {}", e.getMessage());
        }
        this.maxRetry = configured;
        logger.info("TestRetryAnalyzer initialized with maxRetry = {}", this.maxRetry);
    }

    @Override
    public boolean retry(ITestResult result) {
        // If no retries configured, return false quickly
        if (maxRetry <= 0) {
            return false;
        }

        // Inspect the throwable to determine whether this failure is transient (retryable)
        Throwable t = result.getThrowable();
        if (t == null) {
            logger.debug("No throwable found for result {}; not retrying.", result.getName());
            return false;
        }

        // If failure is non-transient (e.g., AssertionError or explicit SkipException) do not retry
        if (!isTransient(t)) {
            logger.info("Not retrying '{}' because failure is non-transient: {} - {}", result.getName(),
                    t.getClass().getSimpleName(), truncateMessage(t.getMessage()));
            // Log to Extent as well if a test is present
            try {
                ExtentTestManager.logStatus(Status.FAIL, "Not retrying: " + t.getClass().getSimpleName() + " - " + truncateMessage(t.getMessage()));
            } catch (Exception e) {
                logger.debug("Failed to log non-retry reason to Extent: {}", e.getMessage());
            }
            // clear any stored artifacts (let main flow handle cleanup) — but do not delete files here aggressively
            return false;
        }

        // At this point, failure is transient; apply retry count logic
        if (attempt < maxRetry) {
            attempt++;
            logger.warn("Retrying test '{}' (attempt {}/{}) due to transient error: {} - {}",
                    result.getName(), attempt, maxRetry, t.getClass().getSimpleName(), truncateMessage(t.getMessage()));

            // Log attempt + exception to Extent
            try {
                ExtentTestManager.logStatus(Status.WARNING, "Retry attempt " + attempt + " of " + maxRetry + " for test: " + result.getName());
                ExtentTestManager.logStatus(Status.WARNING, "Reason: " + t.getClass().getSimpleName() + " - " + truncateMessage(t.getMessage()));

                // Also attach a small inline message in the current ExtentTest for quick visibility
                try {
                    ExtentTestManager.getTest().log(Status.INFO, "🔁 Retrying (attempt " + attempt + "): " +
                            t.getClass().getSimpleName() + " - " + truncateMessage(t.getMessage()));
                } catch (Exception e) {
                    // ignore - best-effort
                    logger.debug("Could not add inline retry log to ExtentTest: {}", e.getMessage());
                }
            } catch (Exception e) {
                logger.debug("Failed to log retry details to Extent: {}", e.getMessage());
            }

            // Prefer live screenshot if driver still available; otherwise attempt to attach saved temp screenshot
            try {
                WebDriver driver = DriverFactory.getDriver();
                if (driver != null) {
                    ExtentTestManager.captureScreenshot(driver, "Retry_" + result.getName() + "_attempt" + attempt);
                } else {
                    Path tmp = RetryStorage.getScreenshotPath();
                    if (tmp != null && Files.exists(tmp)) {
                        try {
                            byte[] bytes = Files.readAllBytes(tmp);
                            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
                            ExtentTestManager.logStatus(Status.WARNING, "Attaching saved failure screenshot for retry attempt " + attempt);
                            ExtentTestManager.getTest().log(Status.INFO,
                                    "📸 Saved screenshot (before teardown):<br><img src='data:image/png;base64," + base64 + "' width='700' style='border:1px solid #ccc;'/>");
                            try {
                                ExtentTestManager.getTest().addScreenCaptureFromPath(tmp.toAbsolutePath().toString());
                            } catch (Exception e) {
                                logger.debug("Could not add file-based screenshot to Extent: {}", e.getMessage());
                            }
                        } catch (Exception e) {
                            logger.warn("Failed reading temp screenshot file for retry: {}", e.getMessage());
                        }
                    } else {
                        logger.warn("Driver is null and no temp screenshot available for retry (test: {}).", result.getName());
                    }
                }
            } catch (Exception e) {
                logger.warn("Unexpected error during retry handling for {}: {}", result.getName(), e.getMessage());
            }

            return true;
        }

        // We've exhausted retries. Clean up temp artifact if present and clear storage.
        try {
            Path p = RetryStorage.getScreenshotPath();
            if (p != null && Files.exists(p)) {
                try {
                    Files.deleteIfExists(p);
                    logger.debug("Deleted temporary retry screenshot after max retries: {}", p.toAbsolutePath());
                } catch (Exception e) {
                    logger.debug("Unable to delete temporary retry screenshot after max retries: {}", e.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.debug("Error clearing RetryStorage temp file: {}", ex.getMessage());
        } finally {
            RetryStorage.clearAll();
        }

        return false;
    }

    /**
     * Determine whether the given throwable is likely transient and worth retrying.
     * We consider common timing, session and network-related exceptions as transient.
     *
     * Do NOT retry on:
     * - AssertionError (test assertion failures)
     * - SkipException (explicit test skips)
     *
     * Retry on:
     * - org.openqa.selenium.TimeoutException
     * - org.openqa.selenium.StaleElementReferenceException
     * - org.openqa.selenium.NoSuchSessionException
     * - org.openqa.selenium.WebDriverException (only when it looks like a transient session/network issue)
     * - java.net.SocketTimeoutException, ConnectException, UnknownHostException
     * - ElementClickInterceptedException, NoSuchWindowException (these may be transient in many flows)
     *
     * @param t throwable to inspect
     * @return true if transient (retryable), false otherwise
     */
    private boolean isTransient(Throwable t) {
        if (t == null) {
            return false;
        }

        // Do not retry on clear assertion / test skip
        if (t instanceof AssertionError) {
            return false;
        }
        if (t instanceof SkipException) {
            return false;
        }

        // Selenium types - check by class name to be defensive if versions differ
        String cls = t.getClass().getName();

        // Known retryable Selenium exceptions
        if (cls.contains("TimeoutException") ||
                cls.contains("StaleElementReferenceException") ||
                cls.contains("NoSuchSessionException") ||
                cls.contains("NoSuchWindowException") ||
                cls.contains("NoSuchElementException") ||
                cls.contains("ElementClickInterceptedException")) {
            return true;
        }

        // WebDriverException can be transient (driver crashed, session lost, remote connection hiccup)
        if (t instanceof org.openqa.selenium.WebDriverException) {
            // Inspect message for network/session keywords that indicate transient issues
            String m = (t.getMessage() == null) ? "" : t.getMessage().toLowerCase();
            if (m.contains("session") || m.contains("connection") || m.contains("socket") || m.contains("timed out") || m.contains("unexpected") || m.contains("chrome not reachable") || m.contains("net::")) {
                return true;
            }
            // otherwise conservative: do NOT retry for generic WebDriverException unless message indicates network/session
            return false;
        }

        // Network-related exceptions
        if (t instanceof SocketTimeoutException || t instanceof ConnectException || t instanceof UnknownHostException) {
            return true;
        }

        // Fallback: if cause exists, inspect recursively (some frameworks wrap the underlying exception)
        Throwable cause = t.getCause();
        if (cause != null && cause != t) {
            return isTransient(cause);
        }

        // Default: not transient
        return false;
    }

    private String truncateMessage(String msg) {
        if (msg == null) return "";
        if (msg.length() <= 300) return msg;
        return msg.substring(0, 300) + "...";
    }
}
