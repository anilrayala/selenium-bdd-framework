package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import reports.ExtentTestManager;

import java.util.Map;

public class StudentFormPage extends BasePage {

    /* ---------- Locators ---------- */

    private final By firstName = By.id("firstName");
    private final By lastName = By.id("lastName");
    private final By email = By.id("userEmail");
    private final By mobileNumber = By.id("userNumber");
    private final By subjectsInput = By.id("subjectsInput");
    private final By currentAddress = By.id("currentAddress");

    /* ---------- Navigation ---------- */

    public void openStudentForm() {
        navigateTo("https://demoqa.com/automation-practice-form");
    }

    /* ---------- Actions ---------- */

    public void fillTextFields(Map<String, String> data) {

        sendKeys(firstName, data.get("firstName"));
        ExtentTestManager.logStatus(Status.INFO, "First Name entered: " + data.get("firstName"));
        sendKeys(lastName, data.get("lastName"));
        ExtentTestManager.logStatus(Status.INFO, "Last Name entered: " + data.get("lastName"));

        sendKeys(email, data.get("email"));
        ExtentTestManager.logStatus(Status.INFO, "Email entered: " + data.get("email"));

        sendKeys(mobileNumber, data.get("mobileNumber"));
        ExtentTestManager.logStatus(Status.INFO, "Mobile Number entered: " + data.get("mobileNumber"));


        // Subjects auto-suggest (press Enter)
        if (data.get("subjects") != null && !data.get("subjects").isBlank()) {
            sendKeys(subjectsInput, data.get("subjects"));
            subjectsInputEnter();
            ExtentTestManager.logStatus(Status.INFO, "Subject entered: " + data.get("subjects"));

        }

        sendKeys(currentAddress, data.get("currentAddress"));
        ExtentTestManager.logStatus(Status.INFO, "Current Address entered: " + data.get("currentAddress"));

    }

    private void subjectsInputEnter() {
        driver.findElement(subjectsInput).sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    /* ---------- Validations ---------- */

    public boolean areTextFieldsPopulated(Map<String, String> data) {

        return getAttributeValue(firstName).equals(data.get("firstName"))
                && getAttributeValue(lastName).equals(data.get("lastName"))
                && getAttributeValue(email).equals(data.get("email"))
                && getAttributeValue(mobileNumber).equals(data.get("mobileNumber"))
                && getAttributeValue(currentAddress).equals(data.get("currentAddress"));
    }

    private String getAttributeValue(By locator) {
        return wait.waitForVisibility(locator).getAttribute("value");
    }
}