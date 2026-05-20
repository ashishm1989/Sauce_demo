package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil — takes and saves screenshots for test reporting.
 *
 * Screenshots are stored under the configured directory with a timestamped
 * filename so they never overwrite each other.
 */
public class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtil() { /* utility class */ }

    /**
     * Captures a screenshot for the current thread's driver.
     *
     * @param testName  used as part of the filename
     * @return absolute path of the saved screenshot, or null on failure
     */
    public static String capture(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (!(driver instanceof TakesScreenshot)) {
                log.warn("Driver does not support screenshots.");
                return null;
            }

            // Ensure directory exists
            String dirPath = config.getScreenshotDir();
            Files.createDirectories(Paths.get(dirPath));

            // Build unique filename
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String fileName  = safeName + "_" + timestamp + ".png";
            Path dest        = Paths.get(dirPath, fileName);

            // Copy from temp location to destination
            File temp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(temp.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            log.info("Screenshot saved: {}", dest.toAbsolutePath());
            return dest.toAbsolutePath().toString();

        } catch (IOException e) {
            log.error("Failed to save screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Could not capture screenshot (driver may be closed): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Returns the screenshot as a Base64-encoded string (useful for embedding
     * directly into Extent Reports without a file dependency).
     */
    public static String captureBase64(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (!(driver instanceof TakesScreenshot)) return null;
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.warn("Could not capture base64 screenshot for '{}': {}", testName, e.getMessage());
            return null;
        }
    }
}
