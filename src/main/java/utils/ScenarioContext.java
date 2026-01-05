package utils;

/**
 * Holds scenario-level metadata for reporting and logging in ThreadLocal.
 * This is the SINGLE SOURCE OF TRUTH for scenario name
 * across Hooks, RetryAnalyzer, and Extent.
 */
public final class ScenarioContext {

    private static final ThreadLocal<String> scenarioName = new ThreadLocal<>();
    private static final ThreadLocal<String> dataIdentifier = new ThreadLocal<>();

    private ScenarioContext() {}

    public static void setScenarioName(String name) {
        scenarioName.set(name);
    }

    public static String getScenarioName() {
        return scenarioName.get();
    }

    public static void setDataIdentifier(String id) {
        dataIdentifier.set(id);
    }

    public static String getDataIdentifier() {
        return dataIdentifier.get();
    }

    public static void clear() {
        scenarioName.remove();
        dataIdentifier.remove();
    }
}