package reports;

import com.aventstack.extentreports.*;
import factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.ScenarioContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.json.JSONObject;
import java.nio.file.StandardOpenOption;

public class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final ExtentReports extent = ExtentManager.createInstance();

    private static int passCount = 0;
    private static int failCount = 0;
    private static int skipCount = 0;
    private static int warningCount = 0;

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    private static ExtentTest getTestSafe() {

        ExtentTest test = extentTest.get();

        if (test == null) {
            String scenarioName = ScenarioContext.getScenarioName();
            String name = scenarioName != null
                    ? scenarioName + " (Retry)"
                    : "Unknown Scenario (Retry)";

            test = extent.createTest(name);
            extentTest.set(test);
        }
        return test;
    }

    public static void startTest(String testName) {
        extentTest.set(extent.createTest(testName));
    }

    public static void logStatus(Status status, String message) {
        getTestSafe().log(status, message);

        switch (status) {
            case PASS -> passCount++;
            case FAIL -> failCount++;
            case SKIP -> skipCount++;
            case WARNING -> warningCount++;
            default -> {}
        }
    }

    public static void captureScreenshot(WebDriver driver, String screenshotName) {

        if (driver == null) {
            driver = DriverFactory.getDriver();
        }

        if (!(driver instanceof TakesScreenshot ts)) {
            getTestSafe().log(Status.WARNING, "Screenshot not supported.");
            return;
        }

        try {
            byte[] bytes = ts.getScreenshotAs(OutputType.BYTES);
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String safeName = screenshotName.replaceAll("[\\\\/:*?\"<>|]", "_");

            Path dir = Path.of(System.getProperty("user.dir"), "reports", "screenshots");
            Files.createDirectories(dir);

            Path file = dir.resolve(safeName + "_" + timestamp + ".png");
            Files.write(file, bytes);

            getTestSafe().log(Status.INFO,
                    "📸 Screenshot:<br><img src='data:image/png;base64," + base64 +
                            "' width='700'/>");

        } catch (Exception e) {
            getTestSafe().log(Status.WARNING, "Screenshot failed: " + e.getMessage());
        }
    }

    public static void addSummary() {

        try {
            Path reportsDir = Path.of(System.getProperty("user.dir"), "reports");
            Files.createDirectories(reportsDir);

            JSONObject obj = new JSONObject(Map.of(
                    "passed", passCount,
                    "failed", failCount,
                    "skipped", skipCount,
                    "warnings", warningCount
            ));

            Files.writeString(
                    reportsDir.resolve("summary.json"),
                    obj.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (Exception e) {
            System.err.println("Unable to write summary.json: " + e.getMessage());
        }

        extent.flush();
    }
}
