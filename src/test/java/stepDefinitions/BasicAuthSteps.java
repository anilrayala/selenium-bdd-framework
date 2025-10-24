package stepDefinitions;

import base.BaseTest;
import factory.DriverFactory;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import pages.BasicAuthPage;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class BasicAuthSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(BasicAuthSteps.class);
    private BasicAuthPage basicAuthPage;

    @Given("user navigates to the internet home page")
    public void user_navigates_to_home_page() {
        driver = DriverFactory.getDriver();
        basicAuthPage = new BasicAuthPage(driver);
        basicAuthPage.openHomePage();
    }

    @When("user clicks on the Basic Auth link")
    public void user_clicks_on_basic_auth_link() {
        basicAuthPage.clickBasicAuthLink();
    }

    @And("user enters username {string} and password {string} in the popup")
    public void user_enters_credentials(String username, String password) {
        basicAuthPage.handleAuthentication(username, password);
    }

    @Then("verify the Basic Auth success message is displayed")
    public void verify_basic_auth_success() {
        String pageContent = basicAuthPage.getPageContent();
        Assertions.assertTrue(pageContent.contains("Congratulations"),
                "Login failed! 'Congratulations' text not found.");
        ExtentTestManager.captureScreenshot(driver, "Successful login");
        ExtentTestManager.logStatus(Status.PASS, "Verified Basic Auth success message.");
        logger.info("Successfully verified Basic Auth login message.");
    }
}
