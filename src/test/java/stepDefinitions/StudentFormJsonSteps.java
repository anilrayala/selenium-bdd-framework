package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import pages.StudentFormPage;
import utils.ScenarioContext;
import utils.TestDataContext;
import utils.TestDataLoader;

import java.util.Map;

public class StudentFormJsonSteps {

    private final StudentFormPage studentFormPage = new StudentFormPage();

    @Given("user fills student form using json {string} with key {string}")
    public void userFillsStudentFormUsingJsonKey(
            String fileName,
            String key
    ) {
        Map<String, String> data =
                TestDataLoader.loadJsonRecord(fileName, key);

        studentFormPage.openStudentForm();
        studentFormPage.fillTextFields(data);
    }

    @Then("student form text fields should be populated correctly using json data")
    public void studentFormTextFieldsShouldBePopulatedCorrectly() {

        Map<String, String> data = TestDataContext.get();

        Assert.assertTrue(
                studentFormPage.areTextFieldsPopulated(data),
                "One or more student form text fields are NOT populated correctly"
        );
    }
}
