package base;

import factory.DriverFactory;
import org.openqa.selenium.WebDriver;

public class BaseTest {
    protected WebDriver driver;

    public void setUp() {
        // ✅ only fetch existing driver, don’t initialize new
        this.driver = DriverFactory.getDriver();
    }

    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
