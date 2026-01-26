package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
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

    @When("user uploads profile picture {string} json data")
    public void userUploadsProfilePicture(String fileName) {
        studentFormPage.uploadProfilePicture("testdata/files/" + fileName);
    }

    @When("user selects date of birth {string} {string} {string} and select gender hobby json data")
    public void userSelectsDateOfBirth(String day, String month, String year) {
        studentFormPage.selectGenderAndHobby();
        studentFormPage.selectDateOfBirth(day, month, year);
    }

    @Then("student form should be populated correctly using json data")
    public void studentFormShouldBePopulatedCorrectly() {

        SoftAssert sa = new SoftAssert();
        Map<String, String> expected = TestDataContext.get();

        Map<String, String> actual = studentFormPage.getTextFieldValues();

        sa.assertEquals(actual.get("firstName"), expected.get("firstName"), "First Name mismatch");
        sa.assertEquals(actual.get("lastName"), expected.get("lastName"), "Last Name mismatch");
        sa.assertEquals(actual.get("email"), expected.get("email"), "Email mismatch");
        sa.assertEquals(actual.get("mobileNumber"), expected.get("mobileNumber"), "Mobile Number mismatch");
        sa.assertEquals(actual.get("currentAddress"), expected.get("currentAddress"), "Address mismatch");

        sa.assertEquals(studentFormPage.getGender(), "Male", "Gender mismatch");
        sa.assertTrue(studentFormPage.getDateOfBirth().contains("1997"), "DOB mismatch");

        sa.assertTrue(
                studentFormPage.getSubjects().contains(expected.get("subjects")),
                "Subject mismatch"
        );

        sa.assertEquals(studentFormPage.getState(), expected.get("state"), "State mismatch");
        sa.assertEquals(studentFormPage.getCity(), expected.get("city"), "City mismatch");

        sa.assertAll();
    }
}
