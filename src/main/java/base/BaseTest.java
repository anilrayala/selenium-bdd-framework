package base;

import factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    public void setUp() {
        ConfigReader.loadConfig(); // load config
        driver = DriverFactory.initializeDriver();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                ConfigReader.getInt("implicitWait")));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
                ConfigReader.getInt("pageLoadTimeout")));

//        String baseUrl = ConfigReader.getProperty("baseUrl");
//        if (baseUrl != null && !baseUrl.isEmpty()) {
//            driver.get(baseUrl);
//        }
    }

    public void tearDown() {
        DriverFactory.quitDriver();
    }
}

