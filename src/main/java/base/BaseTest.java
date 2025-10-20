package base;

import factory.DriverFactory;
import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

public class BaseTest {
    protected WebDriver driver;

    public void setUp() {
        ConfigReader.loadConfig(); // ensure config loaded
        // ✅ only fetch existing driver, don’t initialize new
        this.driver = DriverFactory.getDriver();
    }

    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
