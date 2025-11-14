package utils;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Attach TestRetryAnalyzer to all @Test methods automatically.
 * Also replaces TestNG's DisabledRetryAnalyzer when present (common with Cucumber generated methods).
 */
public class RetryListener implements IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(RetryListener.class);

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        String methodName = (testMethod == null) ? "null" : testMethod.getName();
        String className = (testClass == null) ? "null" : testClass.getName();

        try {
            Class<? extends IRetryAnalyzer> current = annotation.getRetryAnalyzerClass();
            String currentName = (current == null) ? "null" : current.getName();

            // Debug log for visibility (console + logger)
            System.out.println("[RetryListener] transform called for class=" + className + " method=" + methodName);
            System.out.println("[RetryListener] current retry analyzer: " + currentName);
            logger.debug("transform called for class={}, method={}, currentRetryAnalyzer={}",
                    className, methodName, currentName);

            // If current is null, IRetryAnalyzer.class, or TestNG's DisabledRetryAnalyzer,
            // then attach our TestRetryAnalyzer.
            boolean isDisabled = currentName != null && currentName.toLowerCase().contains("disabledretryanalyzer");
            boolean isDefaultIRetry = current == null || current.equals(IRetryAnalyzer.class);

            if (isDefaultIRetry || isDisabled) {
                annotation.setRetryAnalyzer(TestRetryAnalyzer.class);
                System.out.println("[RetryListener] set TestRetryAnalyzer on: " + methodName);
                logger.info("Set TestRetryAnalyzer on method: {} (class: {})", methodName, className);
            } else {
                logger.debug("Leaving existing retry analyzer '{}' for method {}", currentName, methodName);
            }
        } catch (Throwable t) {
            // Defensive: never fail test run because of listener
            System.err.println("[RetryListener] transform error: " + t.getMessage());
            logger.warn("RetryListener.transform error: {}", t.getMessage(), t);
        }
    }
}
