package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import reports.ExtentTestManager;

public class Buttons extends BasePage {

    public static final Logger logger = LogManager.getLogger(Buttons.class);

    private final By doubleClickBtn = By.id("doubleClickBtn");
    private final By doubleClickMessage = By.id("doubleClickMessage");
    private final By rightClickBtn = By.id("rightClickBtn");
    private final By rightClickMessage = By.id("rightClickMessage");
    private final By dynamicClickBtn = By.xpath("//button[text()='Click Me']");
    private final By dynamicClickMessage = By.id("dynamicClickMessage");

    public Buttons() {
        super();
    }

    public void openButtonsPage() {
        logger.info("Opening Buttons page");
        ExtentTestManager.logStatus(Status.INFO, "Opening Buttons page");
        String url = "https://demoqa.com/buttons";
        navigateTo(url);
    }

    public void doubleClickButton() {
        scrollIntoView(doubleClickBtn);
        doubleClick(doubleClickBtn);
        ExtentTestManager.logStatus(Status.INFO, "Performed double click");
    }

    public void rightClickButton() {
        scrollIntoView(rightClickBtn);
        rightClick(rightClickBtn);
        ExtentTestManager.logStatus(Status.INFO, "Performed right click");
    }

    public void dynamicClickButton() {
        scrollIntoView(dynamicClickBtn);
        click(dynamicClickBtn);
        ExtentTestManager.logStatus(Status.INFO, "Performed dynamic click");
    }

    public String getDoubleClickMessage() {
        scrollIntoView(doubleClickMessage);
        String text = getText(doubleClickMessage);
        ExtentTestManager.logStatus(Status.INFO, "Fetched Double Click Message: "  + text);
        return text;
    }

    public String getRightClickMessage() {
        scrollIntoView(rightClickMessage);
        String text = getText(rightClickMessage);
        ExtentTestManager.logStatus(Status.INFO, "Fetched Right Click Message: "  + text);
        return text;
    }

    public String getDynamicClickMessage() {
        scrollIntoView(dynamicClickMessage);
        String text = getText(dynamicClickMessage);
        ExtentTestManager.logStatus(Status.INFO, "Fetched Dynamic Click Message: "  + text);
        return text;
    }
}
