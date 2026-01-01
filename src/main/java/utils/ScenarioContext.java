package utils;

/**
 * Holds scenario-related metadata in ThreadLocal.
 * This is the SINGLE SOURCE OF TRUTH for scenario name
 * across Hooks, RetryAnalyzer, and Extent.
 */
public final class ScenarioContext {

    private static final ThreadLocal<String> scenarioName = new ThreadLocal<>();

    private ScenarioContext() {}

    public static void setScenarioName(String name) {
        scenarioName.set(name);
    }

    public static String getScenarioName() {
        return scenarioName.get();
    }

    public static void clear() {
        scenarioName.remove();
    }
}