package stepDefinitions;

import base.BaseTest;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import pages.Buttons;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class ButtonsSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(ButtonsSteps.class);
    private Buttons buttons;

    @Given("user navigates to the buttons page")
    public void user_navigates_to_buttons_page() {
        buttons = new Buttons();
        buttons.openButtonsPage();
        logger.info("Navigated to Buttons page");
        ExtentTestManager.logStatus(Status.INFO, "Navigated to Buttons page");
    }

    @When("user performs double click on the button")
    public void user_double_clicks_button() {
        buttons.doubleClickButton();
    }

    @Then("verify double click message is displayed")
    public void verify_double_click_message() {
        String actual = buttons.getDoubleClickMessage();
        Assert.assertEquals(actual, "You have done a double click",
                "Double click message mismatch");
        ExtentTestManager.logStatus(Status.PASS, "Double click message verified");
    }

    @When("user performs right click on the button")
    public void user_right_clicks_button() {
        buttons.rightClickButton();
    }

    @Then("verify right click message is displayed")
    public void verify_right_click_message() {
        String actual = buttons.getRightClickMessage();
        Assert.assertEquals(actual, "You have done a right click",
                "Right click message mismatch");
        ExtentTestManager.logStatus(Status.PASS, "Right click message verified");
    }

    @And("user performs dynamic click on the button and verify message")
    public void user_dynamic_click_and_verify() {
        buttons.dynamicClickButton();
        String actual = buttons.getDynamicClickMessage();
        Assert.assertEquals(actual, "You have done a dynamic click",
                "Dynamic click message mismatch");
        ExtentTestManager.logStatus(Status.PASS, "Dynamic click message verified");
    }
}
