package utils;

import java.util.Map;

public final class TestDataContext {

    private static final ThreadLocal<Map<String, String>> DATA =
            new ThreadLocal<>();

    private TestDataContext() {}

    public static void set(Map<String, String> data) {
        DATA.set(data);
    }

    public static Map<String, String> get() {
        return DATA.get();
    }

    public static void clear() {
        DATA.remove();
    }
}