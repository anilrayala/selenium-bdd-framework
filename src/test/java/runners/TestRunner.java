package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import utils.RetryListener;

/**
 * TestNG Cucumber runner with Allure + Extent integration.
 * Preserves parallel execution and retry listener.
 */
@Listeners({RetryListener.class})
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // ✅ Added Allure plugin
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

        @Override
        @DataProvider(parallel = true) // ✅ Keep parallel execution configurable
        public Object[][] scenarios() {
                return super.scenarios();
        }
}