package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import pages.StudentFormPage;
import utils.ScenarioContext;
import utils.TestDataContext;
import utils.TestDataLoader;

import java.util.Map;

public class StudentFormExcelSteps {

    private final StudentFormPage studentFormPage = new StudentFormPage();

    @Given("user fills student form using row {string} from {string} sheet")
    public void userFillsStudentFormUsingRowFromSheet(String row, String sheetName) {

        int rowNumber;
        try {
            rowNumber = Integer.parseInt(row);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid row number in Scenario Outline: " + row
            );
        }

        // Load data via reusable utility
        Map<String, String> data =
                TestDataLoader.loadExcelRow(rowNumber, sheetName);

        studentFormPage.openStudentForm();
        studentFormPage.fillTextFields(data);
    }

    @Then("student form text fields should be populated correctly using excel data")
    public void studentFormTextFieldsShouldBePopulatedCorrectly() {

        Map<String, String> data = TestDataContext.get();

        Assert.assertTrue(
                studentFormPage.areTextFieldsPopulated(data),
                "One or more student form text fields are NOT populated correctly"
        );
    }
}
