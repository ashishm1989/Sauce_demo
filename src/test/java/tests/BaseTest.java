package tests;

import listeners.RetryAnalyzer;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.DriverFactory;

/**
 * BaseTest — parent for all test classes.
 *
 * Responsibilities:
 *  - Open/close the browser around each test method (@BeforeMethod / @AfterMethod)
 *  - Expose shared logger and config to subclasses
 *  - Centralise the RetryAnalyzer so every @Test in every subclass uses it
 */
@Listeners({listeners.TestListener.class, listeners.ExtentReportListener.class})
public abstract class BaseTest {

    protected final Logger        log    = LoggerFactory.getLogger(getClass());
    protected final ConfigReader  config = ConfigReader.getInstance();

    /* ------------------------------------------------------------------ */
    /*  Browser lifecycle                                                   */
    /* ------------------------------------------------------------------ */

    /**
     * Initialise a fresh WebDriver before every test method.
     * Browser / headless values come from config or Maven -D flags.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        log.info("─────────────────────────────────────────");
        log.info("Setting up WebDriver  [thread={}]", Thread.currentThread().getId());
        DriverFactory.initDriver();
    }

    /**
     * Quit the driver after every test method — whether it passed or failed.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Tearing down WebDriver [thread={}]", Thread.currentThread().getId());
        DriverFactory.quitDriver();
        log.info("─────────────────────────────────────────");
    }
}
