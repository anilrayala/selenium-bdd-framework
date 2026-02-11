package stepDefinitions;

import io.cucumber.java.en.*;
import org.testng.asserts.SoftAssert;
import pages.StudentFormPage;
import utils.TestDataContext;
import utils.TestDataLoader;

import java.util.Map;

public class StudentFormExcelSteps {

    private final StudentFormPage studentFormPage = new StudentFormPage();

    @Given("user fills student form using row {string} from {string} sheet")
    public void userFillsStudentFormUsingRowFromSheet(String row, String sheetName) {

        int rowNumber = Integer.parseInt(row);
        Map<String, String> data =
                TestDataLoader.loadExcelRow(rowNumber, sheetName);

        studentFormPage.openStudentForm();
        studentFormPage.fillTextFields(data);
    }

    @When("user uploads profile picture {string}")
    public void userUploadsProfilePicture(String fileName) {
        studentFormPage.uploadProfilePicture("testdata/files/" + fileName);
    }

    @When("user selects date of birth {string} {string} {string} and select gender hobby")
    public void userSelectsDateOfBirth(String day, String month, String year) {
        studentFormPage.selectGenderAndHobby();
        studentFormPage.selectDateOfBirth(day, month, year);
    }

    @Then("student form should be populated correctly using excel data")
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
