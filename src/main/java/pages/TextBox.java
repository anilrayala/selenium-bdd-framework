package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;

public class TextBox extends BasePage {

    private static final Logger logger = LogManager.getLogger(TextBox.class);

    private final By fullName = By.id("userName");
    private final By email = By.id("userEmail");
    private final By currentAddress = By.id("currentAddress");
    private final By permanentAddress = By.id("permanentAddress");
    private final By submit = By.xpath("//div/button[@id='submit']");

    private final By outputName = By.id("name");
    private final By outputEmail = By.id("email");
    private final By outputCurrentAddress = By.xpath("//div/p[@id='currentAddress']");
    private final By outputPermanentAddress = By.xpath("//div/p[@id='permanentAddress']");

    public TextBox() {
        super();
    }

    public void openTextBoxPage() {
        logger.info("Opening TextBox page");
        ExtentTestManager.logStatus(Status.INFO, "Opening TextBox page");
        String url = "https://demoqa.com/text-box";
        navigateTo(url);
    }

    public void enterFullName(String value) {
        logger.info("Entering full name");
        ExtentTestManager.logStatus(Status.INFO, "Entering Full Name: " + value);

        sendKeys(fullName, value);
    }

    public void enterEmail(String value) {
        logger.info("Entering email");
        ExtentTestManager.logStatus(Status.INFO, "Entering Email: " + value);

        sendKeys(email, value);
    }

    public void enterCurrentAddress(String value) {
        logger.info("Entering current address");
        ExtentTestManager.logStatus(Status.INFO, "Entering Current Address");

        sendKeys(currentAddress, value);
    }

    public void enterPermanentAddress(String value) {
        logger.info("Entering permanent address");
        ExtentTestManager.logStatus(Status.INFO, "Entering Permanent Address");

        sendKeys(permanentAddress, value);
    }

    public void clickSubmit() {
        logger.info("Clicking Submit button");
        ExtentTestManager.logStatus(Status.INFO, "Clicking Submit button");
        click(submit);
    }

    public String getDisplayedName() {
        String text = getText(outputName);
        ExtentTestManager.logStatus(Status.INFO, "Fetched displayed Name: " + text);
        return text;
    }

    public String getDisplayedEmail() {
        String text = getText(outputEmail);
        ExtentTestManager.logStatus(Status.INFO, "Fetched displayed Email: " + text);
        return text;
    }

    public String getDisplayedCurrentAddress() {
        String text = getText(outputCurrentAddress);
        ExtentTestManager.logStatus(Status.INFO, "Fetched displayed Current Address: "  + text);
        return text;
    }

    public String getDisplayedPermanentAddress() {
        String text = getText(outputPermanentAddress);
        ExtentTestManager.logStatus(Status.INFO, "Fetched displayed Permanent Address: " + text);
        return text;
    }
}
