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
import utils.ConfigReader;
import utils.ScenarioContext;

import java.io.ByteArrayInputStream;

public class Hooks extends BaseTest {

    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeScenario(Scenario scenario) {

        // Thread-safe scenario context
        ScenarioContext.setScenarioName(scenario.getName());

        // MDC for logging
        ThreadContext.put("scenario", scenario.getName());

        logger.info("Starting scenario: {}", scenario.getName());

        ConfigReader.loadConfig();
        DriverFactory.initializeDriver();
        setUp();

        // Create Extent test
        ExtentTestManager.startTest(scenario.getName());

        ExtentTestManager.getTest()
                .assignAuthor(System.getProperty("user.name"))
                .assignDevice(ConfigReader.getProperty("browser"))
                .assignCategory(scenario.getSourceTagNames().toArray(new String[0]));
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

        ExtentTestManager.logStatus(
                scenario.isFailed() ? Status.FAIL : Status.PASS,
                "Scenario finished: " + scenario.getName()
        );

        tearDown();

        // Cleanup
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