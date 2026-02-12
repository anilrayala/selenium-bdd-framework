package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.ConfigReader;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    public static WebDriver initializeDriver() {

        if (driver.get() == null) {

            // ConfigReader already handles:
            // -Dexecution
            // ENV variable
            // config file
            String execution = ConfigReader.getProperty("execution");
            String browser = ConfigReader.getProperty("browser").toLowerCase();

            logger.info("Execution mode: {}", execution);
            logger.info("Browser: {}", browser);

            if ("remote".equalsIgnoreCase(execution)) {
                driver.set(initRemoteDriver(browser));
            } else {
                driver.set(initLocalDriver(browser));
            }

            WebDriver webDriver = driver.get();

            webDriver.manage().window().maximize();
            webDriver.manage().deleteAllCookies();

            webDriver.manage().timeouts().implicitlyWait(
                    Duration.ofSeconds(ConfigReader.getInt("implicitWait"))
            );

            webDriver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout"))
            );

            logger.info("Driver initialized successfully.");
        }

        return driver.get();
    }

    // ---------------- LOCAL DRIVER ----------------

    private static WebDriver initLocalDriver(String browser) {

        boolean headless = ConfigReader.getBoolean("headless");

        switch (browser) {

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                return new FirefoxDriver(firefoxOptions);

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless=new");
                return new EdgeDriver(edgeOptions);

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(chromeOptions);
        }
    }

    // ---------------- REMOTE DRIVER (BrowserStack) ----------------

    private static WebDriver initRemoteDriver(String browser) {

        try {

            String username = ConfigReader.getProperty("bs.username");
            String accessKey = ConfigReader.getProperty("bs.accessKey");
            String baseUrl = ConfigReader.getProperty("bs.url");

            if (username == null || accessKey == null) {
                throw new RuntimeException("BrowserStack credentials missing!");
            }

            String hubUrl = baseUrl.replace(
                    "https://",
                    "https://" + username + ":" + accessKey + "@"
            );

            switch (browser) {

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    setBrowserStackCapabilities(firefoxOptions);
                    return new RemoteWebDriver(new URL(hubUrl), firefoxOptions);

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    setBrowserStackCapabilities(edgeOptions);
                    return new RemoteWebDriver(new URL(hubUrl), edgeOptions);

                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    setBrowserStackCapabilities(chromeOptions);
                    return new RemoteWebDriver(new URL(hubUrl), chromeOptions);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize remote driver", e);
        }
    }

    private static void setBrowserStackCapabilities(MutableCapabilities options) {

        options.setCapability("browserVersion",
                ConfigReader.getProperty("bs.browserVersion"));

        options.setCapability("bstack:options", Map.of(
                "os", ConfigReader.getProperty("bs.os"),
                "osVersion", ConfigReader.getProperty("bs.osVersion"),
                "buildName", "BDD_Framework_Build",
                "sessionName", Thread.currentThread().getName()
        ));
    }

    // ---------------- GET / QUIT ----------------

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {

        WebDriver drv = driver.get();

        if (drv != null) {
            logger.info("Quitting driver for thread: {}", Thread.currentThread().getName());
            try {
                drv.quit();
            } catch (Exception e) {
                logger.warn("Exception while quitting driver: {}", e.getMessage(), e);
            } finally {
                driver.remove();
            }
        }else {
            logger.info("quitDriver() called but driver was already null for this thread.");
        }
    }
}
