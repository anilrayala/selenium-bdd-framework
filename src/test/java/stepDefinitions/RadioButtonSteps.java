package stepDefinitions;

import base.BaseTest;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import pages.RadioButton;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class RadioButtonSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(RadioButtonSteps.class);
    private RadioButton radioButton;

    @Given("user navigates to the radio button page")
    public void user_navigates_to_radio_button_page() {
        radioButton = new RadioButton();
        radioButton.openRadioButtonPage();
        logger.info("Navigated to RadioButton page");
        ExtentTestManager.logStatus(Status.INFO, "Navigated to RadioButton page");
    }

    @When("user clicks on the Yes radio button")
    public void user_clicks_yes_radio_button() {
        radioButton.clickYesRadio();
        ExtentTestManager.logStatus(Status.INFO, "Yes radio button clicked");
    }

    @Then("verify the success text is displayed as Yes")
    public void verify_yes_success_text() {
        String actualText = radioButton.getSuccessText();
        Assert.assertEquals(actualText, "Yes", "Success text mismatch for Yes");
        ExtentTestManager.logStatus(Status.PASS, "Verified success text as Yes");
    }

    @When("user clicks on the Impressive radio button")
    public void user_clicks_impressive_radio_button() {
        radioButton.clickImpressiveRadio();
        ExtentTestManager.logStatus(Status.INFO, "Impressive radio button clicked");
    }

    @Then("verify the success text is displayed as Impressive")
    public void verify_impressive_success_text() {
        String actualText = radioButton.getSuccessText();
        Assert.assertEquals(actualText, "Impressive", "Success text mismatch for Impressive");
        ExtentTestManager.logStatus(Status.PASS, "Verified success text as Impressive");
    }

    @And("verify the No radio button is disabled")
    public void verify_no_radio_button_disabled() {
        Assert.assertTrue(radioButton.isNoRadioDisabled(),
                "No radio button should be disabled");
        ExtentTestManager.logStatus(Status.PASS, "Verified No radio button is disabled");
    }
}
