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
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class WaitHelper {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(WaitHelper.class);

    public WaitHelper() {
        this(DriverFactory.getDriver());
    }

    public WaitHelper(WebDriver driver) {
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver is null. Ensure DriverFactory.initializeDriver() was called."
            );
        }
        this.driver = driver;
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(ConfigReader.getInt("explicitWait"))
        );
    }

    /* ---------- Core Waits ---------- */

    public WebElement waitForVisibility(By locator) {
        log("Waiting for visibility: " + locator);
        return performWait(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        log("Waiting for clickability: " + locator);
        return performWait(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForPresence(By locator) {
        log("Waiting for presence: " + locator);
        return performWait(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> waitForAllPresence(By locator) {
        log("Waiting for presence of all elements: " + locator);
        return performWait(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public boolean waitForInvisibility(By locator) {
        log("Waiting for invisibility: " + locator);
        return performWait(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public Alert waitForAlert() {
        log("Waiting for alert");
        return performWait(ExpectedConditions.alertIsPresent());
    }

    /* ---------- Validation Waits ---------- */

    public boolean waitForTextToBe(By locator, String text) {
        log("Waiting for text '" + text + "' in " + locator);
        return performWait(ExpectedConditions.textToBe(locator, text));
    }

    public boolean waitForAttributeToContain(By locator, String attr, String value) {
        log("Waiting for attribute '" + attr + "' to contain '" + value + "'");
        return performWait(
                ExpectedConditions.attributeContains(driver.findElement(locator), attr, value)
        );
    }

    public boolean waitForAttributeToBe(By locator, String attr, String value) {
        log("Waiting for attribute '" + attr + "' to be '" + value + "'");
        return performWait(
                ExpectedConditions.attributeToBe(driver.findElement(locator), attr, value)
        );
    }

    public boolean waitForCssValue(By locator, String property, String expected) {
        log("Waiting for CSS '" + property + "' to be '" + expected + "'");
        return performWait(d ->
                d.findElement(locator)
                        .getCssValue(property)
                        .equalsIgnoreCase(expected)
        );
    }

    /* ---------- Framework / App-level waits ---------- */

    public void waitForPageToLoad() {
        log("Waiting for page load");
        performWait(d ->
                Objects.equals(((JavascriptExecutor) d)
                        .executeScript("return document.readyState"), "complete")
        );
    }

    public void waitForAjaxToComplete() {
        log("Waiting for AJAX to complete");
        performWait(d -> {
            try {
                Object active = ((JavascriptExecutor) d)
                        .executeScript("return window.jQuery ? jQuery.active : 0");
                return active instanceof Long && (Long) active == 0;
            } catch (Exception e) {
                return true; // jQuery not present
            }
        });
    }

    public boolean waitForTitleContains(String title) {
        return performWait(ExpectedConditions.titleContains(title));
    }

    public boolean waitForUrlContains(String urlPart) {
        return performWait(ExpectedConditions.urlContains(urlPart));
    }

    /* ---------- Stability ---------- */

    public boolean waitForElementToBeStable(By locator, int stableMillis, int pollMillis, int timeoutSec) {
        long end = System.currentTimeMillis() + timeoutSec * 1000L;
        long stableUntil = 0;
        String lastSnapshot = null;

        while (System.currentTimeMillis() < end) {
            try {
                WebElement el = driver.findElement(locator);
                String snapshot = el.getAttribute("outerHTML");

                if (snapshot.equals(lastSnapshot)) {
                    if (stableUntil == 0) stableUntil = System.currentTimeMillis() + stableMillis;
                    if (System.currentTimeMillis() >= stableUntil) return true;
                } else {
                    stableUntil = 0;
                }
                lastSnapshot = snapshot;
            } catch (Exception ignored) {
                stableUntil = 0;
            }
            sleep(pollMillis);
        }
        return false;
    }

    /* ---------- Core performWait ---------- */

    public <T> T performWait(Function<WebDriver, T> condition) {
        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            handleWaitFailure("Timeout waiting for condition", e);
            throw e;
        } catch (Exception e) {
            handleWaitFailure("Wait condition failed", e);
            throw e;
        }
    }

    /* ---------- Utilities ---------- */

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleWaitFailure(String message, Exception e) {
        logger.error("{}: {}", message, e.getMessage(), e);
        ExtentTestManager.logStatus(Status.FAIL, message + ": " + e.getMessage());
        ExtentTestManager.captureScreenshot(driver, "WaitFailure_" + System.currentTimeMillis());
    }

    private void log(String msg) {
        logger.info(msg);
        ExtentTestManager.logStatus(Status.INFO, msg);
    }
}
