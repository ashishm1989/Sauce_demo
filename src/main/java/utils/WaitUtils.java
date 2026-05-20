package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * WaitUtils — centralised explicit-wait helpers.
 *
 * All methods use the driver from DriverFactory (thread-local), so they
 * are safe to call from any page object without passing a driver reference.
 */
public class WaitUtils {

    private static final Logger log = LoggerFactory.getLogger(WaitUtils.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private WaitUtils() { /* utility class */ }

    /* ------------------------------------------------------------------ */
    /*  Element visibility / presence                                      */
    /* ------------------------------------------------------------------ */

    /** Wait until the element is visible on screen. */
    public static WebElement waitForVisible(WebElement element) {
        log.debug("Waiting for element to be visible: {}", element);
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    /** Wait until an element identified by By is visible. */
    public static WebElement waitForVisible(By locator) {
        log.debug("Waiting for locator to be visible: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Wait until an element is present in the DOM (not necessarily visible). */
    public static WebElement waitForPresence(By locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /* ------------------------------------------------------------------ */
    /*  Clickability                                                        */
    /* ------------------------------------------------------------------ */

    /** Wait until element is clickable (visible + enabled). */
    public static WebElement waitForClickable(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public static WebElement waitForClickable(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    /* ------------------------------------------------------------------ */
    /*  Text / URL / Title conditions                                       */
    /* ------------------------------------------------------------------ */

    /** Wait until the page URL contains the given fragment. */
    public static boolean waitForUrlContains(String urlFragment) {
        return getWait().until(ExpectedConditions.urlContains(urlFragment));
    }

    /** Wait until the element's text contains the given substring. */
    public static boolean waitForTextPresent(WebElement element, String text) {
        return getWait().until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    /* ------------------------------------------------------------------ */
    /*  Invisibility / staleness                                            */
    /* ------------------------------------------------------------------ */

    /** Wait until an element disappears from the DOM or becomes invisible. */
    public static boolean waitForInvisibility(By locator) {
        return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /* ------------------------------------------------------------------ */
    /*  Page-ready helpers                                                  */
    /* ------------------------------------------------------------------ */

    /** Spin until document.readyState == "complete". */
    public static void waitForPageLoad() {
        getWait().until(driver ->
                "complete".equals(((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")));
    }

    /* ------------------------------------------------------------------ */
    /*  Custom timeout overloads                                            */
    /* ------------------------------------------------------------------ */

    public static WebElement waitForVisible(WebElement element, int timeoutSeconds) {
        return buildWait(timeoutSeconds).until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickable(WebElement element, int timeoutSeconds) {
        return buildWait(timeoutSeconds).until(ExpectedConditions.elementToBeClickable(element));
    }

    /* ------------------------------------------------------------------ */
    /*  Internal helpers                                                    */
    /* ------------------------------------------------------------------ */

    private static WebDriverWait getWait() {
        return new WebDriverWait(DriverFactory.getDriver(),
                Duration.ofSeconds(config.getExplicitWait()));
    }

    private static WebDriverWait buildWait(int seconds) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(seconds));
    }
}
