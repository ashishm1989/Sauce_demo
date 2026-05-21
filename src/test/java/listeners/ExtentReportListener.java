package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

import java.io.File;


public class ExtentReportListener implements ITestListener, ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(ExtentReportListener.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    
    private static ExtentReports extent;

    
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    

    @Override
    public void onStart(ISuite suite) {
        String reportPath = config.getReportPath();
        new File(reportPath).getParentFile().mkdirs();   // ensure directory exists

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("SauceDemo Automation Report");
        spark.config().setReportName("Test Execution Report");
        spark.config().setTheme(Theme.DARK);
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        spark.config().setEncoding("UTF-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Application", "SauceDemo");
        extent.setSystemInfo("Base URL", config.getBaseUrl());
        extent.setSystemInfo("Browser", config.getBrowser());
        extent.setSystemInfo("Headless", String.valueOf(config.isHeadless()));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));

        log.info("ExtentReports initialised → {}", reportPath);
    }

    @Override
    public void onFinish(ISuite suite) {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReport written → {}", config.getReportPath());
        }
    }

    

    @Override
    public void onTestStart(ITestResult result) {
        String fullName = result.getTestClass().getRealClass().getSimpleName()
                + " → " + result.getName();
        ExtentTest test = extent.createTest(fullName);

        if (result.getMethod().getGroups() != null) {
            for (String group : result.getMethod().getGroups()) {
                test.assignCategory(group);
            }
        }

        testNode.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getCurrentTest().log(Status.PASS, "Test PASSED ✔");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = getCurrentTest();
        test.log(Status.FAIL, "Test FAILED ✘");
        test.log(Status.FAIL, result.getThrowable());

        // Embed screenshot if captured by TestListener
        String base64 = (String) result.getAttribute("screenshotBase64");
        if (base64 != null) {
            try {
                test.fail("Screenshot on failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
            } catch (Exception e) {
                log.warn("Could not embed screenshot in report: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        getCurrentTest().log(Status.SKIP, "Test SKIPPED ⚠");
        if (result.getThrowable() != null) {
            getCurrentTest().log(Status.SKIP, result.getThrowable());
        }
    }

  
    private ExtentTest getCurrentTest() {
        return testNode.get();
    }
}
