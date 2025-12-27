package stepDefinitions;

import base.BaseTest;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import pages.Checkbox;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class CheckboxSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(CheckboxSteps.class);
    private Checkbox checkbox;

    @Given("user navigates to the checkbox page")
    public void user_navigates_to_checkbox_page() {
        checkbox = new Checkbox();
        checkbox.openCheckboxPage();
        logger.info("Navigated to Checkbox page");
        ExtentTestManager.logStatus(Status.INFO, "Navigated to Checkbox page");
    }

    @When("user clicks on the homeToggle and desktopToggle")
    public void user_clicks_home_and_desktop_toggle() {
        checkbox.expandHomeAndDesktop();
        ExtentTestManager.logStatus(Status.PASS, "Expanded Home and Desktop toggles");
    }

    @Then("user clicks on commands checkbox and verify text")
    public void user_clicks_commands_and_verify() {
        checkbox.selectCommandsCheckbox();
        String actualText = checkbox.getCommandsText();
        Assert.assertEquals(actualText.toLowerCase(), "commands",
                "Commands text mismatch");
        ExtentTestManager.logStatus(Status.PASS,
                "Commands checkbox verified with text: " + actualText);
    }

    @And("user clicks on documentsToggle and react checkbox")
    public void user_clicks_documents_and_react() {
        checkbox.expandDocumentsAndSelectReact();
        ExtentTestManager.logStatus(Status.PASS, "Documents expanded and React selected");
    }

    @Then("verify the reactText")
    public void verify_react_text() {
        String actualText = checkbox.getReactText();
        Assert.assertEquals(actualText.toLowerCase(), "react",
                "React text mismatch");
        ExtentTestManager.logStatus(Status.PASS,
                "React checkbox verified with text: " + actualText);
    }
}
