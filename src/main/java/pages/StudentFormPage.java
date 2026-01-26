package pages;

import com.aventstack.extentreports.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import reports.ExtentTestManager;
import utils.FileUtil;

import java.util.*;

public class StudentFormPage extends BasePage {

    /* ---------- Locators ---------- */

    private final By firstName = By.id("firstName");
    private final By lastName = By.id("lastName");
    private final By email = By.id("userEmail");
    private final By mobileNumber = By.id("userNumber");
    private final By subjectsInput = By.id("subjectsInput");
    private final By currentAddress = By.id("currentAddress");

    private final By genderLabel = By.xpath("//label[text()='Male']");
    private final By hobbyLabel  = By.xpath("//label[text()='Sports']");

    private final By uploadPicture = By.id("uploadPicture");

    private final By dobInput = By.id("dateOfBirthInput");
    private final By monthDropdown = By.className("react-datepicker__month-select");
    private final By yearDropdown  = By.className("react-datepicker__year-select");

    private final By stateValue = By.xpath("//div[@id='state']//div[contains(@class,'singleValue')]");
    private final By cityValue  = By.xpath("//div[@id='city']//div[contains(@class,'singleValue')]");

    private final By stateInput = By.id("react-select-3-input");
    private final By cityInput  = By.id("react-select-4-input");

    /* ---------- Navigation ---------- */

    public void openStudentForm() {
        navigateTo("https://demoqa.com/automation-practice-form");
    }

    /* ---------- Actions ---------- */

    public void fillTextFields(Map<String, String> data) {
        sendKeys(firstName, data.get("firstName"));
        sendKeys(lastName, data.get("lastName"));
        sendKeys(email, data.get("email"));
        sendKeys(mobileNumber, data.get("mobileNumber"));
        sendKeys(currentAddress, data.get("currentAddress"));

        selectSubjects(data.get("subjects"));
        selectStateAndCity(data.get("state"), data.get("city"));

        ExtentTestManager.logStatus(Status.INFO, "Filled all text fields");
    }

    public void selectGenderAndHobby() {
        click(genderLabel);
        click(hobbyLabel);
    }

    public void selectSubjects(String subjects) {
        if (subjects == null || subjects.isBlank()) return;

        for (String subject : subjects.split(",")) {
            sendKeys(subjectsInput, subject.trim());
            driver.findElement(subjectsInput).sendKeys(Keys.ENTER);
        }
    }

    public void selectStateAndCity(String state, String city) {
        sendKeys(stateInput, state);
        driver.findElement(stateInput).sendKeys(Keys.ENTER);

        sendKeys(cityInput, city);
        driver.findElement(cityInput).sendKeys(Keys.ENTER);
    }

    public void uploadProfilePicture(String resourcePath) {
        String absolutePath = FileUtil.getFileFromResources(resourcePath);
        driver.findElement(uploadPicture).sendKeys(absolutePath);
    }

    public void selectDateOfBirth(String day, String month, String year) {
        click(dobInput);
        new Select(wait.waitForVisibility(monthDropdown)).selectByVisibleText(month);
        new Select(wait.waitForVisibility(yearDropdown)).selectByVisibleText(year);

        By dayLocator = By.xpath(
                "//div[contains(@class,'react-datepicker__day') and text()='"
                        + Integer.parseInt(day) + "']"
        );
        click(dayLocator);
    }

    /* ---------- Getters (Actual UI Values) ---------- */

    public Map<String, String> getTextFieldValues() {
        Map<String, String> actual = new HashMap<>();
        actual.put("firstName", getValue(firstName));
        actual.put("lastName", getValue(lastName));
        actual.put("email", getValue(email));
        actual.put("mobileNumber", getValue(mobileNumber));
        actual.put("currentAddress", getValue(currentAddress));
        return actual;
    }

    public String getGender() {
        return wait.waitForVisibility(genderLabel).getText();
    }

    public String getDateOfBirth() {
        return wait.waitForVisibility(dobInput).getAttribute("value");
    }

    public List<String> getSubjects() {
        return driver.findElements(By.className("subjects-auto-complete__multi-value__label"))
                .stream().map(WebElement::getText).toList();
    }

    public String getState() {
        return wait.waitForVisibility(stateValue).getText();
    }

    public String getCity() {
        return wait.waitForVisibility(cityValue).getText();
    }

    private String getValue(By locator) {
        return wait.waitForVisibility(locator).getAttribute("value");
    }
}