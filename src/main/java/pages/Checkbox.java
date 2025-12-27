package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import reports.ExtentTestManager;

public class Checkbox extends BasePage {

    private static final Logger logger = LogManager.getLogger(Checkbox.class);

    private final By homeToggle = By.xpath("//button[@title='Toggle']");
    private final By desktopToggle = By.xpath("(//button[@title='Toggle'])[2]");
    private final By commandsCheckbox = By.xpath("//span[text()='Commands']");
    private final By commandsText = By.xpath("//span[text()='commands']");
    private final By documentsToggle = By.xpath("(//button[@title='Toggle'])[3]");
    private final By workspaceToggle = By.xpath("(//button[@title='Toggle'])[4]");
    private final By reactCheckbox = By.xpath("//span[text()='React']");
    private final By reactText = By.xpath("//span[text()='react']");

    public Checkbox() {
        super();
    }

    public void openCheckboxPage() {
        logger.info("Opening Checkbox page");
        ExtentTestManager.logStatus(Status.INFO, "Opening Checkbox page");
        String url = "https://demoqa.com/checkbox";
        navigateTo(url);
        waitForPageLoad();
    }

    public void expandHomeAndDesktop() {
        ExtentTestManager.logStatus(Status.INFO, "Expanding Home and Desktop toggles");
        jsClick(homeToggle);
        jsClick(desktopToggle);
    }

    public void selectCommandsCheckbox() {
        ExtentTestManager.logStatus(Status.INFO, "Selecting Commands checkbox");
        jsClick(commandsCheckbox);
    }

    public String getCommandsText() {
        String text = waitForVisibility(commandsText).getText();
        ExtentTestManager.logStatus(Status.INFO, "Commands text displayed: " + text);
        return text;
    }

    public void expandDocumentsAndSelectReact() {
        ExtentTestManager.logStatus(Status.INFO, "Expanding Documents toggle and selecting React");
        jsClick(documentsToggle);
        jsClick(workspaceToggle);
        jsClick(reactCheckbox);
    }

    public String getReactText() {
        String text = waitForVisibility(reactText).getText();
        ExtentTestManager.logStatus(Status.INFO, "React text displayed: " + text);
        return text;
    }
}
