package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import factory.DriverFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ExtentReports extent = ExtentManager.createInstance();

    private static int passCount = 0;
    private static int failCount = 0;
    private static int skipCount = 0;

    public static synchronized ExtentTest getTest() {
        return extentTest.get();
    }

    private static synchronized ExtentTest getTestSafe() {
        ExtentTest t = extentTest.get();
        if (t == null) {
            t = extent.createTest("FallbackTest_" + System.currentTimeMillis());
            extentTest.set(t);
            t.log(Status.WARNING, "Auto-created fallback ExtentTest because no test was active.");
        }
        return t;
    }

    public static synchronized void startTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
    }

    public static synchronized void logStatus(Status status, String message) {
        ExtentTest test = getTestSafe();
        test.log(status, message);
        switch (status) {
            case PASS -> passCount++;
            case FAIL -> failCount++;
            case SKIP -> skipCount++;
            default -> {}
        }
    }

    /**
     * Robust screenshot capture:
     * 1) Use DriverFactory as fallback
     * 2) Single call to get bytes
     * 3) Write to temp file then move atomically
     * 4) Attach base64 + file path to Extent
     */
    public static synchronized void captureScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            driver = DriverFactory.getDriver();
        }

        ExtentTest test = getTestSafe();

        if (driver == null) {
            test.log(Status.WARNING, "Driver is null — cannot capture screenshot.");
            return;
        }

        if (!(driver instanceof TakesScreenshot)) {
            test.log(Status.WARNING, "Driver does not support screenshots (TakesScreenshot).");
            return;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path screenshotDir = Path.of(System.getProperty("user.dir"), "reports", "screenshots");
            Files.createDirectories(screenshotDir);

            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] bytes = ts.getScreenshotAs(OutputType.BYTES); // single call
            String base64Screenshot = java.util.Base64.getEncoder().encodeToString(bytes);

            Path tempFile = Files.createTempFile(screenshotDir, "tmp_screenshot_", ".png");
            Files.write(tempFile, bytes);

            String safeName = screenshotName.replaceAll("[\\\\/:*?\"<>|]", "_");
            Path destinationPath = screenshotDir.resolve(safeName + "_" + timestamp + ".png");
            Files.move(tempFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            String absPath = destinationPath.toAbsolutePath().toString();
            test.log(Status.INFO, "📁 Screenshot saved to: " + absPath);
            test.log(Status.INFO,
                    "📸 Screenshot:<br><img src='data:image/png;base64," + base64Screenshot +
                            "' width='700' style='border:1px solid #ccc; border-radius:6px;'/>");
            try {
                test.addScreenCaptureFromPath(absPath);
            } catch (Exception e) {
                test.log(Status.INFO, "Failed to add file-based screenshot to Extent: " + e.getMessage());
            }
        } catch (IOException e) {
            getTestSafe().log(Status.WARNING, "Unable to capture screenshot (IO): " + e.getMessage());
        } catch (Exception e) {
            getTestSafe().log(Status.WARNING, "Unexpected error capturing screenshot: " + e.getMessage());
        }
    }

    public static synchronized void endTest() {
        extent.flush();
    }

    public static synchronized void addSummary() {
        ExtentTest summary = extent.createTest("📊 Test Summary");
        try {
            Path reportsDir = Path.of(System.getProperty("user.dir"), "reports");
            Files.createDirectories(reportsDir);
            Path out = reportsDir.resolve("summary.json");
            JSONObject obj = new JSONObject(Map.of("passed", passCount, "failed", failCount, "skipped", skipCount));
            Files.writeString(out, obj.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            summary.log(Status.INFO, "Summary JSON written to: " + out.toAbsolutePath());
        } catch (IOException e) {
            summary.log(Status.WARNING, "Unable to write summary.json: " + e.getMessage());
        }
        summary.log(Status.INFO, "✅ Passed: " + passCount);
        summary.log(Status.INFO, "❌ Failed: " + failCount);
        summary.log(Status.INFO, "⚠️ Skipped: " + skipCount);
        extent.flush();
    }


    public static ExtentReports getExtent() {
        return extent;
    }
}
