package stepDefinitions;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reports.ExtentTestManager;
import factory.DriverFactory;
import utils.ConfigReader;
import utils.RetryStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Hooks - Cucumber lifecycle hooks + improved retry artifact persistence.
 *
 * Key behavior change: do NOT delete the temp screenshot in afterScenario.
 * Keep the temp file on disk so TestRetryAnalyzer can read it when driver is null.
 * TestRetryAnalyzer is responsible for cleaning the temp file after retries finish.
 */
public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        // at the top of beforeScenario
        logger.info("beforeScenario: scenario='{}' thread='{}'", scenario.getName(), Thread.currentThread().getName());
        logger.info("beforeScenario thread: {}", Thread.currentThread().getName());

        ConfigReader.loadConfig();
        DriverFactory.initializeDriver(); // initialize only once per thread
        setUp(); // fetch driver reference into BaseTest.driver
        ExtentTestManager.startTest(scenario.getName());
        logger.info("===== Starting Scenario: {} =====", scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        WebDriver drv = DriverFactory.getDriver(); // single source of truth
        logger.debug("Hooks.afterStep — driver instance: {}", (drv == null ? "null" : drv.getClass().getName()));

        if (scenario.isFailed()) {
            ExtentTestManager.logStatus(Status.FAIL, "Step failed");

            if (drv != null) {
                try {
                    if (drv instanceof TakesScreenshot) {
                        byte[] screenshot = ((TakesScreenshot) drv).getScreenshotAs(OutputType.BYTES);

                        // Attach to cucumber scenario (best-effort)
                        try {
                            scenario.attach(screenshot, "image/png", "Failure Screenshot");
                        } catch (Exception e) {
                            logger.warn("Failed to attach cucumber screenshot bytes: {}", e.getMessage());
                        }

                        // Persist bytes as a temp file under reports/screenshots so retry can use it later.
                        try {
                            Path screenshotsDir = Path.of(System.getProperty("user.dir"), "reports", "screenshots");
                            Files.createDirectories(screenshotsDir);

                            String safeScenarioName = scenario.getName().replaceAll("[\\\\/:*?\"<>|\\s]", "_");
                            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
                            String tmpName = "tmp_retry_" + safeScenarioName + "_" + timestamp + ".png";
                            Path tmpFile = screenshotsDir.resolve(tmpName);

                            // write bytes
                            Files.write(tmpFile, screenshot, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                            // save path in RetryStorage for TestRetryAnalyzer
                            RetryStorage.setScreenshotPath(tmpFile);
                            RetryStorage.setLastFailureMessage("Scenario failed: " + scenario.getName());

                            logger.info("Saved failure screenshot to temp file for retry: {}", tmpFile.toAbsolutePath());
                        } catch (Exception e) {
                            logger.warn("Unable to save screenshot bytes to temp file for retry: {}", e.getMessage());
                        }

                        // Persist to Extent (file + inline) via existing helper (best-effort)
                        try {
                            ExtentTestManager.captureScreenshot(drv, scenario.getName().replaceAll("\\s+", "_"));
                        } catch (Exception e) {
                            logger.warn("Extent screenshot capture failed: {}", e.getMessage());
                        }
                    } else {
                        logger.warn("Driver does not support TakesScreenshot; skipping screenshot capture for failure.");
                    }
                } catch (Exception e) {
                    logger.warn("Failed to capture/attach screenshot bytes in afterStep: {}", e.getMessage());
                }
            } else {
                ExtentTestManager.logStatus(Status.WARNING, "Driver is null — cannot attach or capture screenshot for failed step.");
            }

            logger.error("Step failed: {}", scenario.getName());
        } else {
            ExtentTestManager.logStatus(Status.PASS, "Step passed");
            logger.debug("Step passed");
        }
    }


    @After
    public void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                ExtentTestManager.logStatus(Status.FAIL, "Scenario failed: " + scenario.getName());
            } else {
                ExtentTestManager.logStatus(Status.PASS, "Scenario passed: " + scenario.getName());
            }
        } catch (Exception e) {
            logger.warn("Error while logging scenario status to Extent: {}", e.getMessage());
        }

        // Quit driver for this thread and do minimal cleanup.
        // IMPORTANT: we intentionally DO NOT delete the temp screenshot here because TestRetryAnalyzer
        // may need that file when driver is already quit and retry runs.
        try {
            tearDown();
        } catch (Exception e) {
            logger.warn("Error during tearDown(): {}", e.getMessage());
        }

        // Do NOT delete the temp screenshot here. TestRetryAnalyzer will attempt to attach/delete it.
        // Only clear in-memory references (ThreadLocal), but keep the file path so TestRetryAnalyzer can find it.
        try {
            // Clear the in-memory failure message but leave the screenshot file path available.
            RetryStorage.setLastFailureMessage(null);
        } catch (Exception e) {
            logger.debug("Ignoring error clearing last failure message: {}", e.getMessage());
        }

        logger.info("===== Finished Scenario: {} =====", scenario.getName());
    }

    @AfterAll
    public static void afterAll() {
        ExtentTestManager.addSummary();
        LogManager.getLogger(Hooks.class).info("All scenarios completed. Summary added to report.");
    }
}
