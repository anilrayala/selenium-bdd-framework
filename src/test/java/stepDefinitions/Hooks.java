package stepDefinitions;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import reports.ExtentTestManager;
import factory.DriverFactory;
import utils.ConfigReader;
import utils.RetryStorage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        // ✅ Add MDC for structured logging
        ThreadContext.put("scenario", scenario.getName());

        logger.info("Starting scenario: {}", scenario.getName());
        ConfigReader.loadConfig();
        DriverFactory.initializeDriver();
        setUp();
        ExtentTestManager.startTest(scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        WebDriver drv = DriverFactory.getDriver();

        if (scenario.isFailed()) {
            ExtentTestManager.logStatus(Status.FAIL, "Step failed");

            // ✅ Attach screenshot to Allure
            if (drv != null && drv instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) drv).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
            }

            // ✅ Preserve your existing logic for Extent + retry screenshot persistence
            try {
                if (drv != null) {
                    ExtentTestManager.captureScreenshot(drv, scenario.getName().replaceAll("\\s+", "_"));
                }
            } catch (Exception e) {
                logger.warn("Extent screenshot capture failed: {}", e.getMessage());
            }
        } else {
            ExtentTestManager.logStatus(Status.PASS, "Step passed");
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            ExtentTestManager.logStatus(scenario.isFailed() ? Status.FAIL : Status.PASS,
                    "Scenario finished: " + scenario.getName());
        } catch (Exception e) {
            logger.warn("Error logging scenario status: {}", e.getMessage());
        }

        tearDown();
        RetryStorage.setLastFailureMessage(null);

        // ✅ Clear MDC context
        ThreadContext.clearMap();

        logger.info("Finished scenario: {}", scenario.getName());
    }

    @AfterAll
    public static void afterAll() {
        ExtentTestManager.addSummary();
        LogManager.getLogger(Hooks.class).info("All scenarios completed. Summary added to report.");
    }
}
