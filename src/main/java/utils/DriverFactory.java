package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * DriverFactory — thread-local WebDriver provider.
 *
 * Design goals:
 *  - One driver per thread → safe for parallel test execution
 *  - Browser chosen via config / system property at runtime
 *  - WebDriverManager handles driver binary download automatically
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    /* ------------------------------------------------------------------ */
    /*  Driver lifecycle                                                    */
    /* ------------------------------------------------------------------ */

    /**
     * Initialise and store a WebDriver for the current thread.
     *
     * @param browserName  "chrome" | "firefox" | "edge"  (case-insensitive)
     * @param headless     run in headless mode when true
     */
    public static void initDriver(String browserName, boolean headless) {
        if (driverThread.get() != null) {
            log.warn("Driver already initialised for thread {}. Quitting existing one first.",
                    Thread.currentThread().getId());
            quitDriver();
        }

        WebDriver driver;
        switch (browserName.trim().toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOpts = new FirefoxOptions();
                if (headless) ffOpts.addArguments("--headless");
                driver = new FirefoxDriver(ffOpts);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOpts = new EdgeOptions();
                if (headless) edgeOpts.addArguments("--headless");
                driver = new EdgeDriver(edgeOpts);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOpts = buildChromeOptions(headless);
                driver = new ChromeDriver(chromeOpts);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().window().maximize();

        driverThread.set(driver);
        log.info("Driver initialised: browser={}, headless={}, thread={}",
                browserName, headless, Thread.currentThread().getId());
    }

    /** Convenience overload — reads browser/headless from config. */
    public static void initDriver() {
        initDriver(config.getBrowser(), config.isHeadless());
    }

    /** Returns the WebDriver bound to the current thread. */
    public static WebDriver getDriver() {
        WebDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialised. Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    /** Builds a reusable WebDriverWait with the configured timeout. */
    public static WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(config.getExplicitWait()));
    }

    /** Quits the driver and removes it from the ThreadLocal. */
    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit for thread {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                driverThread.remove();
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /*  ChromeOptions helper                                               */
    /* ------------------------------------------------------------------ */

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions opts = new ChromeOptions();

        if (headless) {
            opts.addArguments("--headless=new");
        }

        // Stability / CI flags
        opts.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-extensions",
                "--disable-popup-blocking",
                "--remote-allow-origins=*"
        );

        // Suppress "Chrome is being controlled…" infobars
        opts.setExperimentalOption("excludeSwitches",
                new String[]{"enable-automation", "enable-logging"});

        return opts;
    }
}
