package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reports.ExtentTestManager;

/**
 * GooglePage — POM using BasePage + WaitHelper
 */
public class GooglePage extends BasePage {

    private static final Logger logger = LogManager.getLogger(GooglePage.class);

    public GooglePage() {
        super(); // BasePage fetches driver + wait
    }

    public void openGoogleHomePage() {
        String url = "https://www.google.com";
        navigateTo(url);
        wait.waitForPageToLoad();
        ExtentTestManager.logStatus(Status.INFO, "Navigated to: " + url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Return title (no screenshot here — avoid side effects).
     * Tests / Steps can call captureScreenshot explicitly if required.
     */
    public String getPageTitle() {
        wait.waitForPageToLoad();
        String title = driver.getTitle();
        ExtentTestManager.logStatus(Status.INFO, "Page title: " + title);
        logger.info("Page title: {}", title);
        return title;
    }
}
