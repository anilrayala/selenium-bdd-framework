package stepDefinitions;

import base.BaseTest;
import pages.BasicAuthPage;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;
import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

/**
 * Steps for the-internet demo pages (Basic Auth + Add/Remove).
 */
public class BasicAuthSteps extends BaseTest {

    private static final Logger logger = LogManager.getLogger(BasicAuthSteps.class);
    private BasicAuthPage basicAuthPage;

    @Given("user navigates to the internet home page")
    public void user_navigates_to_home_page() {
        basicAuthPage = new BasicAuthPage();
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
        Assert.assertTrue(pageContent.contains("Testing"),
                "Login failed! 'Congratulations' text not found.");
        ExtentTestManager.captureScreenshot(driver, "Successful_login");
        ExtentTestManager.logStatus(Status.PASS, "Verified Basic Auth success message.");
        logger.info("Successfully verified Basic Auth login message.");
    }

    @When("user clicks on the Add or Remove Elements link")
    public void user_clicks_on_addOrRemoveElements(){
        basicAuthPage.clickAddOrRemoveElements();
    }

    @And("user clicks on Add Element")
    public void user_clicks_on_addElement(){
        basicAuthPage.clickOnAddElement();
    }

    @Then("verify the Element added")
    public void verify_the_element_added(){
        String deleteText = basicAuthPage.getDeleteButtonText();
        Assert.assertTrue(deleteText.contains("Delete"), "Element is not added");
        ExtentTestManager.captureScreenshot(driver, "Element_Added");
        ExtentTestManager.logStatus(Status.PASS, "Element Added successfully");
        logger.info("Element Added successfully");
    }

    @And("user click on Delete")
    public void user_clicks_on_delete(){
        basicAuthPage.clickDeleteButton();
    }

    @Then("verify the Element is deleted")
    public void verify_the_element_is_deleted(){
        boolean elementDeleted = basicAuthPage.verifyElementIsDeleted();
        Assert.assertTrue(elementDeleted,"❌ Delete button still visible after clicking Delete");
        ExtentTestManager.logStatus(Status.PASS, "✅ Delete button removed as expected");
    }

    // TODO: implement other feature steps (checkboxes, drag-and-drop, dropdown) by adding methods to BasicAuthPage
}
