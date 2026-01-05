package utils;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.*;

public final class ExcelReader {

    private ExcelReader() {}

    public static List<Map<String, String>> getData(
            String resourcePath,
            String sheetName
    ) {

        List<Map<String, String>> dataList = new ArrayList<>();

        try (InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Excel file not found on classpath: " + resourcePath
                );
            }

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                        "Sheet not found: " + sheetName
                );
            }

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();

                for (int j = 0; j < colCount; j++) {
                    String key = headerRow.getCell(j)
                            .getStringCellValue()
                            .trim();
                    String value = getCellValueAsString(row.getCell(j));
                    rowData.put(key, value);
                }

                dataList.add(rowData);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel data", e);
        }

        return dataList;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
