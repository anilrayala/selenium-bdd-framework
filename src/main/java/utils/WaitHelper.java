package utils;

import com.aventstack.extentreports.Status;
import factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import reports.ExtentTestManager;

import java.time.Duration;
import java.util.function.Function;

/**
 * WaitHelper - robust wait utility with logging and Extent integration.
 */
public class WaitHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(WaitHelper.class);

    // Default constructor auto-fetches driver
    public WaitHelper() {
        this(DriverFactory.getDriver());
    }

    // Primary constructor
    public WaitHelper(WebDriver driver) {
        if (driver == null) {
            throw new IllegalStateException("WaitHelper requires a non-null WebDriver. " +
                    "Ensure DriverFactory.initializeDriver() was called before creating page objects.");
        }
        this.driver = driver;

        int explicitWaitTime = ConfigReader.getInt("explicitWait");
        if (explicitWaitTime <= 0) explicitWaitTime = 10; // sensible fallback
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitTime));
    }

    /* ---------- Standard Waits ---------- */

    public WebElement waitForVisibility(By locator) {
        logAction("Waiting for visibility of element: " + locator);
        return performWait(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        logAction("Waiting for element to be clickable: " + locator);
        return performWait(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForPresence(By locator) {
        logAction("Waiting for presence of element: " + locator);
        return performWait(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public boolean waitForInvisibility(By locator) {
        logAction("Waiting for element to become invisible: " + locator);
        return performWait(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public Alert waitForAlert() {
        logAction("Waiting for alert to be present");
        return performWait(ExpectedConditions.alertIsPresent());
    }

    public boolean waitForTitleContains(String title) {
        logAction("Waiting for title to contain: " + title);
        return performWait(ExpectedConditions.titleContains(title));
    }

    public boolean waitForUrlContains(String urlPart) {
        logAction("Waiting for URL to contain: " + urlPart);
        return performWait(ExpectedConditions.urlContains(urlPart));
    }

    /* ---------- Advanced / Custom Waits ---------- */

    public boolean waitForTextToBe(By locator, String expectedText) {
        logAction("Waiting for text '" + expectedText + "' in element: " + locator);
        return performWait(ExpectedConditions.textToBe(locator, expectedText));
    }

    public boolean waitForAttributeToContain(By locator, String attribute, String value) {
        logAction("Waiting for attribute '" + attribute + "' of element " + locator + " to contain: " + value);
        return performWait(ExpectedConditions.attributeContains(driver.findElement(locator), attribute, value));
    }

    public boolean waitForAttributeToBe(By locator, String attribute, String value) {
        logAction("Waiting for attribute '" + attribute + "' of element " + locator + " to be: " + value);
        return performWait(ExpectedConditions.attributeToBe(driver.findElement(locator), attribute, value));
    }

    public boolean waitForCssValue(By locator, String cssProperty, String expectedValue) {
        logAction("Waiting for CSS '" + cssProperty + "' of element " + locator + " to be: " + expectedValue);
        return performWait(d -> {
            String actualValue = d.findElement(locator).getCssValue(cssProperty);
            return actualValue.equalsIgnoreCase(expectedValue);
        });
    }

    public boolean waitForElementToDisappear(WebElement element) {
        logAction("Waiting for element to disappear: " + element);
        return performWait(ExpectedConditions.stalenessOf(element));
    }

    /* ---------- Chainable convenience waits ---------- */

    public WaitHelper waitForPageToLoad() {
        logAction("Waiting for page load completion");
        performWait(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        return this;
    }

    public WaitHelper waitForAjaxToComplete() {
        logAction("Waiting for all AJAX calls to finish");
        performWait(d -> {
            JavascriptExecutor js = (JavascriptExecutor) d;
            try {
                Object activeObj = js.executeScript("return (window.jQuery != undefined) ? jQuery.active : 0");
                if (activeObj instanceof Long) {
                    return ((Long) activeObj) == 0L;
                } else if (activeObj instanceof Number) {
                    return ((Number) activeObj).intValue() == 0;
                } else {
                    return true;
                }
            } catch (Exception e) {
                return true; // no jQuery available
            }
        });
        return this;
    }

    public WaitHelper waitForAngularToLoad() {
        logAction("Waiting for Angular app to stabilize");
        performWait(d -> {
            JavascriptExecutor js = (JavascriptExecutor) d;
            try {
                Boolean isStable = (Boolean) js.executeScript(
                        "return (window.getAllAngularTestabilities) ? window.getAllAngularTestabilities().every(x=>x.isStable()) : true");
                return isStable != null && isStable;
            } catch (Exception e) {
                return true;
            }
        });
        return this;
    }

    /* ---------- Custom timeout support ---------- */

    public WebDriverWait getCustomWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public <T> T performWait(Function<WebDriver, T> condition, int customTimeout) {
        WebDriverWait customWait = getCustomWait(customTimeout);
        try {
            return customWait.until(condition);
        } catch (TimeoutException e) {
            String msg = "⏱ Timeout (" + customTimeout + "s) waiting for condition: " + e.getMessage();
            logger.error(msg);
            ExtentTestManager.logStatus(Status.WARNING, msg);
            ExtentTestManager.captureScreenshot(driver, "WaitFailure_" + System.currentTimeMillis());
            throw e;
        } catch (Exception e) {
            String msg = "❌ Wait condition failed: " + e.getMessage();
            logger.error(msg);
            ExtentTestManager.logStatus(Status.FAIL, msg);
            ExtentTestManager.captureScreenshot(driver, "WaitFailure_" + System.currentTimeMillis());
            throw e;
        }
    }

    /* ---------- Convenience actions ---------- */

    public void clickWhenReady(By locator) {
        WebElement element = waitForClickable(locator);
        element.click();
        logAction("Clicked element: " + locator);
    }

    public void sendKeysWhenVisible(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        logAction("Sent keys '" + text + "' to: " + locator);
    }

    public String getTextWhenVisible(By locator) {
        WebElement element = waitForVisibility(locator);
        logAction("Getting text from: " + locator);
        return element.getText();
    }

    /* ---------- Safe actions with retries ---------- */

    public void safeClick(By locator) {
        int retries = 2;
        while (retries-- > 0) {
            try {
                waitForClickable(locator).click();
                logAction("Safely clicked: " + locator);
                return;
            } catch (StaleElementReferenceException e) {
                logAction("Retrying click due to stale element: " + locator);
            } catch (Exception e) {
                logAction("Retrying click after failure: " + locator);
            }
        }
        throw new RuntimeException("Failed to click after retries: " + locator);
    }

    public void safeType(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
        logAction("Safely entered text '" + text + "' into " + locator);
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
            logAction("Paused for " + millis + "ms");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* ---------- Internal performWait wrapper ---------- */

    private <T> T performWait(Function<WebDriver, T> condition) {
        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            String msg = "⏱ Timeout waiting for condition: " + e.getMessage();
            logger.error(msg);
            ExtentTestManager.logStatus(Status.WARNING, msg);
            ExtentTestManager.captureScreenshot(driver, "WaitFailure_" + System.currentTimeMillis());
            throw e;
        } catch (Exception e) {
            String msg = "❌ Wait condition failed: " + e.getMessage();
            logger.error(msg);
            ExtentTestManager.logStatus(Status.FAIL, msg);
            ExtentTestManager.captureScreenshot(driver, "WaitFailure_" + System.currentTimeMillis());
            throw e;
        }
    }

    private void logAction(String message) {
        logger.info(message);
        ExtentTestManager.logStatus(Status.INFO, message);
    }
}
