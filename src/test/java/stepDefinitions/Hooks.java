package stepDefinitions;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import factory.DriverFactory;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reports.ExtentTestManager;
import utils.ConfigReader;

public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        // Load config and ensure driver exists for this thread
        ConfigReader.loadConfig();
        DriverFactory.initializeDriver();          // initialize driver (if not already)
        setUp();                                   // fetch driver into BaseTest.driver
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
                // Attach screenshot bytes to cucumber scenario
                try {
                    byte[] screenshot = ((TakesScreenshot) drv).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");
                } catch (Exception e) {
                    logger.warn("Failed to attach scenario screenshot bytes: {}", e.getMessage());
                }

                // Persist screenshot to reports & embed in Extent
                try {
                    ExtentTestManager.captureScreenshot(drv, scenario.getName().replaceAll("\\s+", "_"));
                } catch (Exception e) {
                    logger.warn("Extent screenshot capture failed: {}", e.getMessage());
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
        if (scenario.isFailed()) {
            ExtentTestManager.logStatus(Status.FAIL, "Scenario failed: " + scenario.getName());
        } else {
            ExtentTestManager.logStatus(Status.PASS, "Scenario passed: " + scenario.getName());
        }

        // tear down driver for this thread
        try {
            tearDown();
        } catch (Exception e) {
            logger.warn("Exception during tearDown(): {}", e.getMessage());
        }

        logger.info("===== Finished Scenario: {} =====", scenario.getName());
    }

    @AfterAll
    public static void afterAll() {
        ExtentTestManager.addSummary();
        LogManager.getLogger(Hooks.class).info("All scenarios completed. Summary added to report.");
    }
}
