package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import utils.ConfigReader;
import utils.RetryListener;

/**
 * TestNG Cucumber runner with Allure + Extent integration.
 * Preserves parallel execution and retry listener.
 */
@Listeners({RetryListener.class})
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"stepDefinitions"},
        tags = "",
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // ✅ Added Allure plugin
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

        private static final Logger logger = LogManager.getLogger(TestRunner.class);

        @BeforeClass(alwaysRun = true)
        public static void beforeClass() {

                String tagsFromJvm = System.getProperty("cucumber.filter.tags");

                if (tagsFromJvm == null || tagsFromJvm.isBlank()) {
                        logger.info("No cucumber tag filter provided via JVM — CucumberOptions tags will be used.");
                } else {
                        logger.info("Using cucumber tag filter from JVM: {}", tagsFromJvm);
                }
        }

        @Override
        @DataProvider(parallel = true) // ✅ Keep parallel execution configurable
        public Object[][] scenarios() {
                return super.scenarios();
        }
}