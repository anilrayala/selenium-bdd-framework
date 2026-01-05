package utils;

import reports.ExtentTestManager;

import java.util.List;
import java.util.Map;

/**
 * Central test data loader for Excel & JSON.
 * This is the ONLY class that sets TestDataContext.
 */
public final class TestDataLoader {

    private static final String EXCEL_PATH =
            "testdata/testData.xlsx";

    private static final String JSON_BASE_PATH =
            "testdata/json/";

    private TestDataLoader() {}

    /* ===================== Excel support ===================== */

    public static Map<String, String> loadExcelRow(
            int rowNumber,
            String sheetName
    ) {

        List<Map<String, String>> allRows =
                ExcelReader.getData(EXCEL_PATH, sheetName);

        if (rowNumber <= 0 || rowNumber > allRows.size()) {
            throw new IllegalArgumentException(
                    "Invalid Excel row number: " + rowNumber +
                            " (Available rows: " + allRows.size() + ")"
            );
        }

        Map<String, String> data = allRows.get(rowNumber - 1);

        // ✅ DEFAULT data identifier
        ScenarioContext.setDataIdentifier(
                "Excel | " + sheetName + " | Row " + rowNumber
        );
        // ✅ Rename existing child
        ExtentTestManager.updateChildName(
                ScenarioContext.getDataIdentifier()
        );

        TestDataContext.set(data);
        return data;
    }

    /* ===================== JSON support ===================== */

    public static Map<String, String> loadJsonRecord(
            String fileName,
            String recordKey
    ) {

        String fullPath = JSON_BASE_PATH + fileName;

        Map<String, Map<String, String>> allData =
                JsonReader.readJsonAsMap(fullPath);

        if (!allData.containsKey(recordKey)) {
            throw new RuntimeException(
                    "Record key '" + recordKey + "' not found in " + fileName
            );
        }

        Map<String, String> data = allData.get(recordKey);

        // ✅ DEFAULT data identifier
        ScenarioContext.setDataIdentifier(
                "JSON | " + fileName + " | " + recordKey
        );

        ExtentTestManager.updateChildName(
                ScenarioContext.getDataIdentifier()
        );

        TestDataContext.set(data);
        return data;
    }
}
