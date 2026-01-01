package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * RetryListener
 *
 * ✔ Automatically attaches TestRetryAnalyzer to all TestNG tests
 * ✔ Handles Cucumber-generated test methods
 * ✔ Safe: never fails the test run
 */
public class RetryListener implements IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(RetryListener.class);

    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {

        try {
            Class<? extends IRetryAnalyzer> currentRetry =
                    annotation.getRetryAnalyzerClass();

            boolean attachRetry =
                    currentRetry == null
                            || currentRetry.equals(IRetryAnalyzer.class)
                            || currentRetry.getName().toLowerCase()
                            .contains("disabledretryanalyzer");

            if (attachRetry) {
                annotation.setRetryAnalyzer(RetryAnalyzer.class);
                logger.debug(
                        "Attached TestRetryAnalyzer to {}.{}",
                        testClass != null ? testClass.getSimpleName() : "UnknownClass",
                        testMethod != null ? testMethod.getName() : "UnknownMethod"
                );
            }

        } catch (Exception e) {
            logger.warn("RetryListener failed to attach retry analyzer", e);
        }
    }
}
