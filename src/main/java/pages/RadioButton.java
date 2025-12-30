package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import reports.ExtentTestManager;

public class RadioButton extends BasePage {

    private static final Logger logger = LogManager.getLogger(RadioButton.class);

    private final By yesRadio = By.xpath("//label[@for='yesRadio']");
    private final By impressiveRadio = By.xpath("//label[@for='impressiveRadio']");
    private final By noRadio = By.cssSelector("[class=\"custom-control-label disabled\"]");
    private final By successText = By.xpath("//span[@class='text-success']");

    public RadioButton() {
        super();
    }

    public void openRadioButtonPage() {
        logger.info("Opening RadioButton page");
        ExtentTestManager.logStatus(Status.INFO, "Opening RadioButton page");
        String url = "https://demoqa.com/radio-button";
        navigateTo(url);
    }

    public void clickYesRadio() {
        ExtentTestManager.logStatus(Status.INFO, "Clicking Yes radio button");
        click(yesRadio);
    }

    public void clickImpressiveRadio() {
        ExtentTestManager.logStatus(Status.INFO, "Clicking Impressive radio button");
        click(impressiveRadio);
    }

    public String getSuccessText() {
        String text = getText(successText);
        ExtentTestManager.logStatus(Status.INFO, "Success text displayed: " + text);
        return text;
    }

    public boolean isNoRadioDisabled() {
        boolean disabled = !isEnabled(noRadio);
        ExtentTestManager.logStatus(Status.INFO, "No radio button is disabled: " + disabled);
        return disabled;
    }
}
