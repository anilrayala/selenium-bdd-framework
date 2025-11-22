package stepDefinitions;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
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

import java.io.ByteArrayInputStream;

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

            try {
                if (drv != null && drv instanceof TakesScreenshot ts) {
                    byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);

                    // 1️⃣ Attach screenshot to Cucumber (as you already do)
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");

                    // 2️⃣ Attach screenshot to Allure (reliable method)
                    Allure.addAttachment(
                            "Failure Screenshot",
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            "png"
                    );
                }
            } catch (Exception e) {
                logger.warn("Allure/Cucumber screenshot capture failed: {}", e.getMessage());
            }

            // 3️⃣ Screenshot for Extent + Retry System
            try {
                if (drv != null) {
                    ExtentTestManager.captureScreenshot(
                            drv,
                            scenario.getName().replaceAll("\\s+", "_")
                    );
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
