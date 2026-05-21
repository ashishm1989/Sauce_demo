package tests;

import listeners.RetryAnalyzer;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.DriverFactory;


@Listeners({listeners.TestListener.class, listeners.ExtentReportListener.class})
public abstract class BaseTest {

    protected final Logger        log    = LoggerFactory.getLogger(getClass());
    protected final ConfigReader  config = ConfigReader.getInstance();

   
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("─────────────────────────────────────────");
        log.info("Setting up WebDriver  [thread={}]", Thread.currentThread().getId());
        DriverFactory.initDriver();
    }

   
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Tearing down WebDriver [thread={}]", Thread.currentThread().getId());
        DriverFactory.quitDriver();
        log.info("─────────────────────────────────────────");
    }
}
