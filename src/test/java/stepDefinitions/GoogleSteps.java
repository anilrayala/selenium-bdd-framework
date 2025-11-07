package stepDefinitions;

import com.aventstack.extentreports.Status;
import base.BaseTest;
import pages.GooglePage;
import reports.ExtentTestManager;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Step definitions for Google page — lightweight, delegates to GooglePage POM.
 * Assumes Hooks already initialized the driver and BaseTest.setUp() has been called.
 */
public class GoogleSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(GoogleSteps.class);
    private GooglePage googlePage;

    @Given("user launches the browser")
    public void user_launches_the_browser() {
        // Hooks already initialized the driver and BaseTest.setUp() fetches it.
        // Just create the page object (BasePage will pick up driver internally).
        googlePage = new GooglePage();
        ExtentTestManager.logStatus(Status.INFO, "Browser launched successfully");
        logger.info("Browser launched successfully");
    }

    @When("user opens the Google homepage")
    public void user_opens_the_google_homepage() {
        googlePage.openGoogleHomePage();
    }

    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedTitle) {
        String actualTitle = googlePage.getPageTitle();
        ExtentTestManager.logStatus(Status.INFO, "Page title: " + actualTitle);
        logger.info("Page title: {}", actualTitle);

        Assert.assertTrue(actualTitle.contains(expectedTitle),
                "Page title mismatch! Expected to contain: " + expectedTitle);

        ExtentTestManager.logStatus(Status.PASS, "Verified title contains: " + expectedTitle);
        logger.info("Verified title contains: {}", expectedTitle);
    }
}
