package pages;

import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import reports.ExtentTestManager;

/**
 * BasicAuthPage — actions for the-internet demo pages.
 * Uses BasePage methods and WaitHelper.
 */
public class BasicAuthPage extends BasePage {

    private static final Logger logger = LogManager.getLogger(BasicAuthPage.class);

    private final By basicAuthLink = By.xpath("//a[text()='Basic Auth']");
    private final By addRemoveLink = By.xpath("//a[text()='Add/Remove Elements']");
    private final By addElementBtn = By.xpath("//button[text()='Add Element']");
    private final By deleteBtn = By.xpath("//button[text()='Delete']");

    public BasicAuthPage() {
        super();
    }

    public void openHomePage() {
        navigateTo("https://the-internet.herokuapp.com/");
        wait.waitForPageToLoad();
        ExtentTestManager.logStatus(Status.INFO, "Opened the-internet home page");
        logger.info("Opened the-internet home page");
    }

    public void clickBasicAuthLink() {
        click(basicAuthLink); // BasePage.click -> WaitHelper.safeClick
        logger.info("Clicked Basic Auth link");
    }

    /**
     * Preferred approach: embed credentials in URL.
     * Fallback: use alert handling via WaitHelper.waitForAlert()
     */
    public void handleAuthentication(String username, String password) {
        try {
            String target = "https://" + username + ":" + password + "@the-internet.herokuapp.com/basic_auth";
            driver.get(target);
            wait.waitForPageToLoad();
            ExtentTestManager.logStatus(Status.INFO, "Authenticated using URL embedding");
            logger.info("Authenticated via URL embedding");
            return;
        } catch (Exception e) {
            logger.warn("URL-embedding authentication failed; trying alert fallback: {}", e.getMessage());
        }

        // fallback: try alert-based auth (may not work with all browsers)
        try {
            Alert a = wait.waitForAlert();
            // Some browsers don't allow entering credentials via alert; if this works:
            a.sendKeys(username + Keys.TAB + password);
            a.accept();
            wait.waitForPageToLoad();
            ExtentTestManager.logStatus(Status.INFO, "Authenticated via alert sendKeys fallback");
            logger.info("Authenticated via alert fallback");
        } catch (Exception ex) {
            String msg = "Basic Auth fallback failed: " + ex.getMessage();
            ExtentTestManager.logStatus(Status.WARNING, msg);
            logger.warn(msg);
            throw new RuntimeException("Basic Auth failed", ex);
        }
    }

    public void clickAddOrRemoveElements() {
        click(addRemoveLink);
        logger.info("Clicked Add/Remove Elements link");
    }

    public void clickOnAddElement() {
        click(addElementBtn);
        logger.info("Clicked Add Element");
    }

    public String getDeleteButtonText() {
        return getText(deleteBtn);
    }

    public void clickDeleteButton() {
        click(deleteBtn);
    }

    public boolean verifyElementIsDeleted() {
        try {
            wait.waitForInvisibility(deleteBtn);
            logger.info("Delete button invisible => removed");
            return true;
        } catch (Exception e) {
            logger.warn("Delete button still visible");
            return false;
        }
    }

    public String getPageContent() {
        return wait.waitForVisibility(By.tagName("body")).getText();
    }
}
