package stepDefinitions;

import base.BaseTest;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import pages.TextBox;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class TextBoxSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(TextBoxSteps.class);
    private TextBox textBox;

    @Given("user navigates to the textbox page")
    public void user_navigates_to_the_textbox_page() {
        textBox = new TextBox();
        textBox.openTextBoxPage();

        logger.info("Navigated to TextBox page");
        ExtentTestManager.logStatus(Status.INFO, "Navigated to TextBox page");
    }

    @When("user enters {string}, {string}, {string}, {string}")
    public void user_enters_details(String name,
                                    String email,
                                    String currentAddress,
                                    String permanentAddress) {

        ExtentTestManager.logStatus(Status.INFO,
                "Entering user details: " + name + ", " + email);

        textBox.enterFullName(name);
        textBox.enterEmail(email);
        textBox.enterCurrentAddress(currentAddress);
        textBox.enterPermanentAddress(permanentAddress);

        logger.info("Entered all user details");
        ExtentTestManager.logStatus(Status.PASS, "User details entered successfully");
    }

    @And("user clicks on submit")
    public void user_clicks_on_submit() {
        textBox.clickSubmit();

        logger.info("Clicked Submit button");
        ExtentTestManager.logStatus(Status.INFO, "Clicked Submit button");
    }

    @Then("verify submitted details {string}, {string}, {string}, {string}")
    public void verify_submitted_details(String name,
                                         String email,
                                         String currentAddress,
                                         String permanentAddress) {

        ExtentTestManager.logStatus(Status.INFO, "Verifying submitted details");

        Assert.assertTrue(textBox.getDisplayedName().contains(name),
                "Name validation failed");
        ExtentTestManager.logStatus(Status.PASS, "Name validated successfully");

        Assert.assertTrue(textBox.getDisplayedEmail().contains(email),
                "Email validation failed");
        ExtentTestManager.logStatus(Status.PASS, "Email validated successfully");

        Assert.assertTrue(textBox.getDisplayedCurrentAddress().contains(currentAddress),
                "Current Address validation failed");
        ExtentTestManager.logStatus(Status.PASS, "Current Address validated successfully");

        Assert.assertTrue(textBox.getDisplayedPermanentAddress().contains(permanentAddress),
                "Permanent Address validation failed");
        ExtentTestManager.logStatus(Status.PASS, "Permanent Address validated successfully");

        logger.info("All submitted data verified successfully");
    }
}
