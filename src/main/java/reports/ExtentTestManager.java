package reports;

import com.aventstack.extentreports.*;
import factory.DriverFactory;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;
import utils.ScenarioContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExtentTestManager {

    private static final ExtentReports extent =
            ExtentManager.createInstance();

    /* =========================
       Parent & Child storage
       ========================= */

    // One parent per scenario name (shared across threads)
    private static final ConcurrentHashMap<String, ExtentTest> PARENTS =
            new ConcurrentHashMap<>();

    // Execution count per scenario
    private static final ConcurrentHashMap<String, AtomicInteger> EXECUTION_COUNTER =
            new ConcurrentHashMap<>();

    // One child per execution (thread-local)
    private static final ThreadLocal<ExtentTest> CHILD =
            new ThreadLocal<>();

    /* =========================
       Execution counters
       ========================= */

    private static int passCount = 0;
    private static int failCount = 0;
    private static int skipCount = 0;
    private static int warningCount = 0;

    private ExtentTestManager() {}

    /* =========================
       Scenario start
       ========================= */

    public static void startScenario() {

        String scenarioName = ScenarioContext.getScenarioName();
        String dataId = ScenarioContext.getDataIdentifier();

        ExtentTest parent = PARENTS.computeIfAbsent(
                scenarioName,
                name -> {
                    ExtentTest p = extent.createTest(name);
                    p.assignAuthor(System.getProperty("user.name"));
                    p.assignDevice(ConfigReader.getProperty("browser"));
                    return p;
                }
        );

        String childName;

        if (dataId != null && !dataId.isBlank()) {
            // Excel / JSON case
            childName = dataId;
        } else {
            // Scenario Outline / normal case
            int count = EXECUTION_COUNTER
                    .computeIfAbsent(scenarioName, k -> new AtomicInteger(0))
                    .incrementAndGet();

            childName = "Execution " + count;
        }

        ExtentTest child = parent.createNode(childName);
        CHILD.set(child);
    }

    public static void updateChildName(String newName) {

        ExtentTest current = CHILD.get();
        if (current == null) {
            return;
        }

        try {
            // Extent allows renaming by creating a node reference
            current.getModel().setName(newName);
        } catch (Exception ignored) {
            // Safe no-op
        }
    }

    public static ExtentTest getTest() {
        return CHILD.get();
    }

    /* =========================
       Logging
       ========================= */

    public static synchronized void logStatus(Status status, String message) {

        getTest().log(status, message);

        switch (status) {
            case PASS -> passCount++;
            case FAIL -> failCount++;
            case SKIP -> skipCount++;
            case WARNING -> warningCount++;
            default -> {}
        }
    }

    /* =========================
       Screenshot
       ========================= */

    public static void captureScreenshot(
            WebDriver driver,
            String screenshotName
    ) {

        if (driver == null) {
            driver = DriverFactory.getDriver();
        }

        if (!(driver instanceof TakesScreenshot ts)) {
            getTest().log(Status.WARNING,
                    "Driver does not support screenshots");
            warningCount++;
            return;
        }

        try {
            byte[] bytes = ts.getScreenshotAs(OutputType.BYTES);
            String base64 =
                    java.util.Base64.getEncoder().encodeToString(bytes);

            String timestamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss")
                            .format(new Date());

            Path dir = Path.of(
                    System.getProperty("user.dir"),
                    "reports",
                    "screenshots"
            );
            Files.createDirectories(dir);

            Path file = dir.resolve(
                    screenshotName + "_" + timestamp + ".png"
            );
            Files.write(file, bytes);

            getTest().log(
                    Status.INFO,
                    "📸 Screenshot:<br><img src='data:image/png;base64,"
                            + base64 + "' width='700'/>"
            );

        } catch (Exception e) {
            getTest().log(
                    Status.WARNING,
                    "Screenshot failed: " + e.getMessage()
            );
            warningCount++;
        }
    }

    /* =========================
       Scenario end (child level)
       ========================= */

    public static synchronized void finishScenario(boolean failed) {

        if (failed) {
            failCount++;
        } else {
            passCount++;
        }
    }

    /* =========================
       Summary + Flush
       ========================= */

    public static synchronized void addSummary() {

        try {
            Path reportsDir =
                    Path.of(System.getProperty("user.dir"), "reports");
            Files.createDirectories(reportsDir);

            JSONObject summary = new JSONObject(Map.of(
                    "passed", passCount,
                    "failed", failCount,
                    "skipped", skipCount,
                    "warnings", warningCount
            ));

            Files.writeString(
                    reportsDir.resolve("summary.json"),
                    summary.toString(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

        } catch (Exception e) {
            System.err.println(
                    "Unable to write summary.json: " + e.getMessage()
            );
        }

        EXECUTION_COUNTER.clear();
        extent.flush();

    }
}
