package pages;

import factory.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.WaitHelper;

/**
 * 🔹 BasePage — parent for all page objects.
 * Centralizes driver & WaitHelper initialization, and provides
 * common reusable wrapper methods for basic web actions.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WaitHelper wait;

    // ✅ Default constructor fetches driver from DriverFactory
    public BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WaitHelper(this.driver);
    }

    /* ---------------------------
       ✅ Common Wrapper Methods
       --------------------------- */

    protected void click(By locator) {
        wait.safeClick(locator);
    }

    protected void type(By locator, String text) {
        wait.safeType(locator, text);
    }

    protected String getText(By locator) {
        return wait.getTextWhenVisible(locator);
    }

    protected void waitForPageLoad() {
        wait.waitForPageToLoad();
    }

    protected void navigateTo(String url) {
        driver.get(url);
    }

}
