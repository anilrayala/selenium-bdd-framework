package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import reports.ExtentTestManager;
import com.aventstack.extentreports.Status;
//import org.junit.jupiter.api.Assertions;

public class GooglePage {

    private static final Logger logger = LogManager.getLogger(GooglePage.class);
    private WebDriver driver;

    // ✅ Constructor – receive driver from DriverFactory
    public GooglePage(WebDriver driver) {
        this.driver = driver;
    }

    // ✅ Navigate to Google homepage
    public void openGoogleHomePage() {
        String url = "https://www.google.com";
        driver.get(url);
        ExtentTestManager.logStatus(Status.INFO, "Navigated to: " + url);
        logger.info("Navigated to: {}", url);
    }

    /// ✅ Return title for external validation
    public String getPageTitle() {
        String title = driver.getTitle();
        ExtentTestManager.logStatus(Status.INFO, "Page title: " + title);
        logger.info("Page title: {}", title);
        ExtentTestManager.captureScreenshot(driver, "Google Page Title");
        return title;
    }
}
