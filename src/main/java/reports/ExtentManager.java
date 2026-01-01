package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports createInstance() {
        if (extent == null) {

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = System.getProperty("user.dir")
                    + "/reports/ExtentReport_" + timestamp + ".html";

            try {
                Path screenshotsDir = Path.of(
                        System.getProperty("user.dir"), "reports", "screenshots");
                Files.createDirectories(screenshotsDir);
            } catch (IOException e) {
                System.err.println(
                        "Warning: unable to create screenshots directory: " + e.getMessage());
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Automation Report");
            sparkReporter.config().setReportName("BDD Selenium Test Results");

            // ✅ Cosmetic
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("Project", "BDD Selenium Framework");
            extent.setSystemInfo("Environment",
                    System.getProperty("env", "QA").toUpperCase());
            extent.setSystemInfo("Tester", System.getProperty("user.name"));
        }
        return extent;
    }
}
