package utils;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.openqa.selenium.WebDriver;

public class TestBase {

    protected WebDriver driver;

    @Parameters("browser")
    @BeforeClass
    public void setUp(String browserName) {
        driver = DriverFactory.initializeDriver(browserName);
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}