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
            // safe timestamp (no ':' characters)
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport_" + timestamp + ".html";

            // ensure reports/screenshots directory exists up-front
            try {
                Path screenshotsDir = Path.of(System.getProperty("user.dir"), "reports", "screenshots");
                Files.createDirectories(screenshotsDir);
            } catch (IOException e) {
                // don't fail startup; log to stderr
                System.err.println("Warning: unable to create screenshots directory: " + e.getMessage());
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Automation Report");
            sparkReporter.config().setReportName("BDD Selenium Test Results");
            sparkReporter.config().setTheme(Theme.STANDARD);

            // ensure correct encoding and timeline
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Project", "BDD Selenium Framework");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Tester", System.getProperty("user.name"));
        }
        return extent;
    }
}
