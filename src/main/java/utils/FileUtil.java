package utils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileUtil {

    private FileUtil() {}

    /**
     * Loads a file from src/test/resources and
     * copies it to a temporary file for Selenium upload.
     *
     * Works on Windows, Linux, Mac, CI, Docker.
     */
    public static String getFileFromResources(String resourcePath) {

        try (InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (is == null) {
                throw new RuntimeException(
                        "File not found in test resources: " + resourcePath
                );
            }

            // Create temp file
            String fileName = Path.of(resourcePath).getFileName().toString();
            Path tempFile = Files.createTempFile("upload_", "_" + fileName);

            // Copy resource → temp file
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

            return tempFile.toAbsolutePath().toString();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to prepare file for upload: " + resourcePath, e
            );
        }
    }
}
