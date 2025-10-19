package stepDefinitions;

import base.BaseTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import reports.ExtentTestManager;

public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {
        setUp();
        ExtentTestManager.startTest(scenario.getName());
        logger.info("===== Starting Scenario: {} =====", scenario.getName());
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentTestManager.logStatus(Status.FAIL, "Step failed");
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
            ExtentTestManager.captureScreenshot(driver, scenario.getName().replaceAll("\\s+", "_"));
            logger.error("Step failed: {}", scenario.getName());
        } else {
            ExtentTestManager.logStatus(Status.PASS, "Step passed");
            logger.info("Step passed");
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            ExtentTestManager.logStatus(Status.FAIL, "Scenario failed: " + scenario.getName());
        } else {
            ExtentTestManager.logStatus(Status.PASS, "Scenario passed: " + scenario.getName());
        }

        tearDown();
        logger.info("===== Finished Scenario: {} =====", scenario.getName());
    }

    @AfterAll
    public static void afterAll() {
        ExtentTestManager.addSummary();
        LogManager.getLogger(Hooks.class).info("All scenarios completed. Summary added to report.");
    }
}