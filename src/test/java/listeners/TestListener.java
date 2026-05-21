package listeners;

import org.testng.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.ScreenshotUtil;

/**
 * TestListener — hooks into TestNG lifecycle events to:
 *  - Log test start / pass / fail / skip to the console
 *  - Capture a screenshot when a test fails (if enabled in config)
 *  - Print a summary banner to the console after the suite completes
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶  START  : {}.{}",
                result.getTestClass().getName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✔  PASSED  : {} ({}ms)",
                result.getName(), duration(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✘  FAILED  : {} ({}ms) — {}",
                result.getName(),
                duration(result),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no message");

        if (config.isScreenshotOnFailure()) {
            try {
                String path = ScreenshotUtil.capture(result.getName());
                if (path != null) {
                    log.info("   Screenshot: {}", path);
                    // Attach path as attribute so ExtentReportListener can pick it up
                    result.setAttribute("screenshotPath", path);
                    result.setAttribute("screenshotBase64",
                            ScreenshotUtil.captureBase64(result.getName()));
                }
            } catch (Exception e) {
                log.warn("Could not capture screenshot: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠  SKIPPED : {}", result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("~  WITHIN_SUCCESS_PCT : {}", result.getName());
    }

   
    @Override
    public void onStart(ISuite suite) {
        log.info("═══════════════════════════════════════════");
        log.info("  Suite START: {}", suite.getName());
        log.info("═══════════════════════════════════════════");
    }

    @Override
    public void onFinish(ISuite suite) {
        int total   = suite.getResults().values().stream()
                .mapToInt(r -> r.getTestContext().getAllTestMethods().length).sum();

        log.info("═══════════════════════════════════════════");
        log.info("  Suite COMPLETE: {}", suite.getName());
        log.info("  Total test methods : {}", total);
        log.info("═══════════════════════════════════════════");
    }

    

    private long duration(ITestResult r) {
        return r.getEndMillis() - r.getStartMillis();
    }
}
