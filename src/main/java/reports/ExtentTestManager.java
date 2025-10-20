package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ExtentReports extent = ExtentManager.createInstance();

    private static int passCount = 0;
    private static int failCount = 0;
    private static int skipCount = 0;

    public static synchronized ExtentTest getTest() {
        return extentTest.get();
    }

    public static synchronized void startTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    public static synchronized void logStatus(Status status, String message) {
        getTest().log(status, message);
        switch (status) {
            case PASS -> passCount++;
            case FAIL -> failCount++;
            case SKIP -> skipCount++;
            default -> {}
        }
    }

    public static synchronized void captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path screenshotDir = Path.of(System.getProperty("user.dir"), "reports", "screenshots");

            // ✅ Ensure directory exists
            Files.createDirectories(screenshotDir);

            // Take screenshot both as file and Base64
            TakesScreenshot ts = (TakesScreenshot) driver;
            File srcFile = ts.getScreenshotAs(OutputType.FILE);
            String base64Screenshot = ts.getScreenshotAs(OutputType.BASE64);

            // Save a physical copy as well (for manual access if needed)
            Path destinationPath = screenshotDir.resolve(screenshotName + "_" + timestamp + ".png");
            Files.copy(srcFile.toPath(), destinationPath);

            // ✅ Embed inline image in Extent report
            getTest().log(Status.INFO,
                    "📸 Screenshot captured below:<br>" +
                            "<img src='data:image/png;base64," + base64Screenshot +
                            "' width='500' height='340' style='border:1px solid #ccc; border-radius:6px;'/>");

            // Optional: also add to Spark report as attachment
            getTest().addScreenCaptureFromBase64String(base64Screenshot, screenshotName);

        } catch (IOException e) {
            getTest().log(Status.WARNING, "Unable to capture screenshot: " + e.getMessage());
        } catch (Exception e) {
            getTest().log(Status.WARNING, "Unexpected error capturing screenshot: " + e.getMessage());
        }
    }

    public static synchronized void endTest() {
        extent.flush();
    }

    public static synchronized void addSummary() {
        ExtentTest summary = extent.createTest("📊 Test Summary");
        summary.log(Status.INFO, "✅ Passed: " + passCount);
        summary.log(Status.INFO, "❌ Failed: " + failCount);
        summary.log(Status.INFO, "⚠️ Skipped: " + skipCount);
        extent.flush();
    }

    public static ExtentReports getExtent() {
        return extent;
    }
}
