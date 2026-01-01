package utils;

import com.aventstack.extentreports.Status;
import factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.SkipException;
import reports.ExtentTestManager;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);

    private final int maxRetry;
    private int attempt = 0;

    public RetryAnalyzer() {
        int retries;
        try {
            String sysRetry = System.getProperty("retryCount");
            retries = (sysRetry != null && !sysRetry.isBlank())
                    ? Integer.parseInt(sysRetry)
                    : ConfigReader.getInt("retryCount");
        } catch (Exception e) {
            retries = 0;
            logger.warn("Invalid retryCount config. Defaulting to 0.");
        }
        this.maxRetry = retries;
        logger.info("RetryAnalyzer initialized with maxRetry={}", maxRetry);
    }

    @Override
    public boolean retry(ITestResult result) {

        if (attempt >= maxRetry) {
            return false;
        }

        Throwable failure = result.getThrowable();
        if (failure == null || !isTransientFailure(failure)) {
            return false;
        }

        attempt++;

        String scenarioName = ScenarioContext.getScenarioName();
        String nameForLog = scenarioName != null ? scenarioName : "Unknown Scenario";

        logger.warn(
                "Retrying scenario '{}' (attempt {}/{}) due to {}",
                nameForLog, attempt, maxRetry, failure.getClass().getSimpleName()
        );

        ExtentTestManager.logStatus(
                Status.WARNING,
                "🔁 Retrying scenario (attempt " + attempt + "/" + maxRetry + ")"
        );

        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            ExtentTestManager.captureScreenshot(
                    driver,
                    nameForLog.replaceAll("\\s+", "_") + "_Retry_" + attempt
            );
        }

        return true;
    }

    private boolean isTransientFailure(Throwable t) {

        if (t instanceof AssertionError) return false;
        if (t instanceof SkipException) return false;

        String cls = t.getClass().getName();

        if (cls.contains("TimeoutException")
                || cls.contains("StaleElementReferenceException")
                || cls.contains("NoSuchSessionException")
                || cls.contains("NoSuchWindowException")
                || cls.contains("ElementClickInterceptedException")) {
            return true;
        }

        if (t instanceof org.openqa.selenium.WebDriverException) {
            String msg = t.getMessage() == null ? "" : t.getMessage().toLowerCase();
            return msg.contains("session")
                    || msg.contains("connection")
                    || msg.contains("timed out")
                    || msg.contains("chrome not reachable")
                    || msg.contains("net::");
        }

        if (t instanceof SocketTimeoutException
                || t instanceof ConnectException
                || t instanceof UnknownHostException) {
            return true;
        }

        Throwable cause = t.getCause();
        return cause != null && cause != t && isTransientFailure(cause);
    }
}
