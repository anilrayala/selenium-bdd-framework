package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import reports.ExtentTestManager;
import utils.ConfigReader;
import utils.RobotUtils;

public class BasicAuthPage {

    private static final Logger logger = LogManager.getLogger(BasicAuthPage.class);
    private WebDriver driver;

    // Locators
    private By basicAuthLink = By.xpath("//a[contains(text(),'Basic Auth')]");

    public BasicAuthPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Step 1️⃣ Navigate to main homepage
     */
    public void openHomePage() {
        String homeUrl = "https://the-internet.herokuapp.com/";
        driver.get(homeUrl);
        ExtentTestManager.logStatus(Status.INFO, "Navigated to home page: " + homeUrl);
        logger.info("Opened main page: {}", homeUrl);
    }

    /**
     * Step 2️⃣ Click on “Basic Auth” link
     */
    public void clickBasicAuthLink() {
        try {
            WebElement link = driver.findElement(basicAuthLink);
            link.click();
            ExtentTestManager.logStatus(Status.INFO, "Clicked on 'Basic Auth' link");
            logger.info("Clicked on Basic Auth link");
        } catch (Exception e) {
            ExtentTestManager.logStatus(Status.FAIL, "Failed to click Basic Auth link: " + e.getMessage());
            logger.error("Error clicking Basic Auth link", e);
        }
    }

    /**
     * Step 3️⃣ Handle authentication popup or URL with credentials
     */
    public void handleAuthentication(String username, String password) {
        boolean headless = ConfigReader.getBoolean("headless");

        if (headless) {
            // Use embedded credentials (for CI/headless mode)
            openWithEmbeddedCredentials(username, password);
        } else {
            // Use Robot to handle popup (for GUI browsers)
            handlePopupWithRobot(username, password);
        }
    }

    /** ✅ Handle popup using Robot */
    private void handlePopupWithRobot(String username, String password) {
        try {
            RobotUtils.delay(2000); // Wait for popup
            RobotUtils.type(username);
            RobotUtils.pressTab();
            RobotUtils.type(password);
            RobotUtils.pressEnter();

            ExtentTestManager.logStatus(Status.INFO, "Entered credentials using Robot");
            logger.info("Entered credentials using Robot: {} / {}", username, password);
        } catch (Exception e) {
            ExtentTestManager.logStatus(Status.FAIL, "Robot auth failed: " + e.getMessage());
            logger.error("Robot-based auth failed", e);
        }
    }

    /** ✅ Handle authentication using embedded credentials */
    private void openWithEmbeddedCredentials(String username, String password) {
        try {
            String authUrl = "https://" + username + ":" + password + "@the-internet.herokuapp.com/basic_auth";
            driver.get(authUrl);
            ExtentTestManager.logStatus(Status.INFO, "Opened Basic Auth via embedded credentials");
            logger.info("Opened Basic Auth via embedded credentials: {}", authUrl);
        } catch (Exception e) {
            ExtentTestManager.logStatus(Status.FAIL, "Embedded auth failed: " + e.getMessage());
            logger.error("Embedded credentials auth failed", e);
        }
    }

    /** ✅ Return page content for verification */
    public String getPageContent() {
        String content = driver.getPageSource();
        logger.info("Page content length: {}", content.length());
        return content;
    }
}
