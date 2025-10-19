package stepDefinitions;


import com.aventstack.extentreports.Status;
import base.BaseTest;
import reports.ExtentTestManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

public class GoogleSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(GoogleSteps.class);

    @Given("user launches the browser")
    public void user_launches_the_browser() {
        setUp();
        ExtentTestManager.logStatus(Status.INFO, "Browser launched successfully");
        logger.info("Browser launched successfully");
    }

    @When("user opens the Google homepage")
    public void user_opens_the_google_homepage() {
        String url = "https://www.google.com";
        driver.get(url);
        ExtentTestManager.logStatus(Status.INFO, "Navigated to: " + url);
        logger.info("Navigated to: {}", url);
    }

    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedTitle) {
        String actualTitle = driver.getTitle();
        ExtentTestManager.logStatus(Status.INFO, "Page title: " + actualTitle);
        logger.info("Page title: {}", actualTitle);

        Assertions.assertTrue(actualTitle.contains(expectedTitle),
                "Page title mismatch! Expected to contain: " + expectedTitle);
        ExtentTestManager.logStatus(Status.PASS, "Verified title contains: " + expectedTitle);
        logger.info("Verified title contains: {}", expectedTitle);

        tearDown();
    }
}
