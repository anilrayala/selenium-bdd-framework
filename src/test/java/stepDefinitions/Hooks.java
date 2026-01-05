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
import factory.DriverFactory;
import reports.ExtentTestManager;
import utils.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {

        // 1. Scenario metadata
        ScenarioContext.setScenarioName(scenario.getName());
        ThreadContext.put("scenario", scenario.getName());

        logger.info("Starting scenario: {}", scenario.getName());

        // 2. Framework initialization
        ConfigReader.loadConfig();
        DriverFactory.initializeDriver();
        setUp();

        // 3. Create parent + child Extent nodes
        ExtentTestManager.startScenario();
    }

    @AfterStep
    public void afterStep(Scenario scenario) {

        WebDriver driver = DriverFactory.getDriver();

        if (scenario.isFailed()) {

            ExtentTestManager.logStatus(Status.FAIL, "Step failed");

            try {
                if (driver instanceof TakesScreenshot ts) {
                    byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);

                    // Cucumber
                    scenario.attach(screenshot, "image/png", "Failure Screenshot");

                    // Allure
                    Allure.addAttachment(
                            "Failure Screenshot",
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            "png"
                    );

                    // Extent
                    ExtentTestManager.captureScreenshot(
                            driver,
                            scenario.getName().replaceAll("\\s+", "_")
                    );
                }
            } catch (Exception e) {
                logger.warn("Screenshot capture failed: {}", e.getMessage());
            }

        } else {
            ExtentTestManager.logStatus(Status.PASS, "Step passed");
        }
    }

    @After
    public void afterScenario(Scenario scenario) {

        ExtentTestManager.finishScenario(scenario.isFailed());

        tearDown();

        // Cleanup
        TestDataContext.clear();
        ScenarioContext.clear();
        ThreadContext.clearMap();

        logger.info("Finished scenario: {}", scenario.getName());
    }

    @AfterAll
    public static void afterAll() {
        ExtentTestManager.addSummary();
        LogManager.getLogger(Hooks.class)
                .info("All scenarios completed. Summary added to report.");
    }
}