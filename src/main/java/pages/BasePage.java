package pages;

import com.aventstack.extentreports.Status;
import factory.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import reports.ExtentTestManager;
import utils.WaitHelper;

/**
 * 🔹 BasePage — parent for all page objects.
 * Centralizes driver & WaitHelper initialization, and provides
 * common reusable wrapper methods for basic web actions.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WaitHelper wait;
    protected Actions actions;

    // ✅ Default constructor fetches driver from DriverFactory
    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WaitHelper(this.driver);
        this.actions = new Actions(this.driver);
    }

    /* ---------- Navigation ---------- */

    protected void navigateTo(String url) {
        driver.get(url);
        wait.waitForPageToLoad();
        ExtentTestManager.logStatus(Status.INFO, "Navigated to: " + url);
    }

    /* ---------- Core WebDriver-like actions ---------- */

    protected void click(By locator) {
        try {
            wait.waitForClickable(locator).click();
            ExtentTestManager.logStatus(
                    Status.INFO,
                    "Clicked element using normal click: " + locator
            );
        } catch (Exception e) {
            ExtentTestManager.logStatus(
                    Status.WARNING,
                    "Normal click failed for locator: " + locator +
                            " | Falling back to JavaScript click. Reason: " + e.getMessage()
            );
            ExtentTestManager.captureScreenshot(driver,"JS Click Location");
            jsClick(locator);
        }
    }

    protected void sendKeys(By locator, String text) {
        WebElement el = wait.waitForVisibility(locator);
        el.clear();
        el.sendKeys(text);
        ExtentTestManager.logStatus(Status.INFO, "Entered text into: " + locator);
    }

    protected String getText(By locator) {
        String text = wait.waitForVisibility(locator).getText();
        ExtentTestManager.logStatus(Status.INFO, "Read text from: " + locator + " -> " + text);
        return text;
    }

    protected boolean isDisplayed(By locator) {
        return wait.waitForVisibility(locator).isDisplayed();
    }

    protected boolean isEnabled(By locator) {
        return wait.waitForVisibility(locator).isEnabled();
    }

    /* ---------- Actions ---------- */

    protected void doubleClick(By locator) {
        actions.doubleClick(wait.waitForVisibility(locator)).perform();
        ExtentTestManager.logStatus(Status.INFO, "Double clicked: " + locator);
    }

    protected void rightClick(By locator) {
        actions.contextClick(wait.waitForVisibility(locator)).perform();
        ExtentTestManager.logStatus(Status.INFO, "Right clicked: " + locator);
    }

    protected void moveToElement(By locator) {
        actions.moveToElement(wait.waitForVisibility(locator)).perform();
        ExtentTestManager.logStatus(Status.INFO, "Moved to element: " + locator);
    }

    /* ---------- JavaScript helpers ---------- */

    protected void scrollIntoView(By locator) {
        WebElement el = wait.waitForVisibility(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    protected void jsClick(By locator) {
        scrollIntoView(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", wait.waitForVisibility(locator));
        ExtentTestManager.logStatus(Status.INFO, "Clicked using JS: " + locator);
    }

    /* ---------- Wrapped validations ---------- */

    protected boolean waitForText(By locator, String expectedText) {
        return wait.waitForTextToBe(locator, expectedText);
    }

    protected boolean waitUntilStable(By locator) {
        return wait.waitForElementToBeStable(locator, 1000, 200, 10);
    }
}


