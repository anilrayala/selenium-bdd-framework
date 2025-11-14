package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import utils.RetryListener;

/**
 * TestNG Cucumber runner.
 * Extends AbstractTestNGCucumberTests so TestNG runs scenarios.
 *
 * Important: overriding scenarios() and annotating with @DataProvider(parallel = true)
 * enables parallel execution of individual Cucumber scenarios.
 *
 * Make sure your WebDriver/DriverFactory and any shared state are thread-safe (use ThreadLocal).
 */
@Listeners({ RetryListener.class })
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

        /**
         * Enable parallel execution of scenarios.
         *
         * - TestNG will request scenario data via this DataProvider.
         * - Setting parallel = true lets TestNG run the returned scenario array in parallel threads.
         *
         * Adjust TestNG thread-count via testng.xml (thread-count attr) or IDE Run Configuration.
         */
        @Override
        @DataProvider(parallel = false) // allow TestNG to execute scenarios concurrently
        public Object[][] scenarios() {
                return super.scenarios();
        }
}
