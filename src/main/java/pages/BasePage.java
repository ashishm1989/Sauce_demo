package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DriverFactory;
import utils.WaitUtils;

/**
 * BasePage — parent class for all Page Objects.
 *
 * Provides:
 *  - Shared driver/logger reference
 *  - Common interaction helpers (click, type, getText)
 *  - Navigation utility
 *  - JS executor shorthand
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final Logger    log;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.log    = LoggerFactory.getLogger(getClass());
        PageFactory.initElements(driver, this);
    }

    /* ------------------------------------------------------------------ */
    /*  Interaction helpers                                                 */
    /* ------------------------------------------------------------------ */

    /** Wait for an element to be clickable, then click it. */
    protected void click(WebElement element) {
        WaitUtils.waitForClickable(element).click();
        log.debug("Clicked: {}", element);
    }

    /** Clear the field, then type the given text. */
    protected void type(WebElement element, String text) {
        WaitUtils.waitForVisible(element).clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into: {}", text, element);
    }

    /** Return trimmed visible text of an element. */
    protected String getText(WebElement element) {
        return WaitUtils.waitForVisible(element).getText().trim();
    }

    /** Scroll an element into view using JavaScript. */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /** Click an element via JavaScript (useful when overlays block normal click). */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /* ------------------------------------------------------------------ */
    /*  Navigation helpers                                                  */
    /* ------------------------------------------------------------------ */

    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
        WaitUtils.waitForPageLoad();
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }
}
