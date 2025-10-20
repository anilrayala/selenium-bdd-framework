package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigReader;

import java.time.Duration;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver initializeDriver() {
        if (driver.get() == null) { // ✅ create only once
            String browser = ConfigReader.getProperty("browser").toLowerCase();
            boolean headless = ConfigReader.getBoolean("headless");

            switch (browser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions fOpts = new FirefoxOptions();
                    if (headless) fOpts.addArguments("--headless");
                    driver.set(new FirefoxDriver(fOpts));
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions eOpts = new EdgeOptions();
                    if (headless) eOpts.addArguments("--headless=new");
                    driver.set(new EdgeDriver(eOpts));
                    break;

                case "chrome":
                default:
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions cOpts = new ChromeOptions();
                    if (headless) cOpts.addArguments("--headless=new");
                    cOpts.addArguments("--remote-allow-origins=*");
                    driver.set(new ChromeDriver(cOpts));
                    break;
            }

            WebDriver webDriver = driver.get();
            webDriver.manage().window().maximize();
            webDriver.manage().deleteAllCookies();

            // 🕒 Apply waits only once here
            webDriver.manage().timeouts().implicitlyWait(
                    Duration.ofSeconds(ConfigReader.getInt("implicitWait")));
            webDriver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout")));
        }

        return driver.get(); // reuse existing driver
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
