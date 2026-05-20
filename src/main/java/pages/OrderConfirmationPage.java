package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

/**
 * OrderConfirmationPage — Page Object for /checkout-complete.html.
 *
 * This page is shown after a user successfully places an order.
 */
public class OrderConfirmationPage extends BasePage {

    /* ------------------------------------------------------------------ */
    /*  Locators                                                            */
    /* ------------------------------------------------------------------ */

    @FindBy(className = "complete-header")
    private WebElement confirmationHeader;

    @FindBy(className = "complete-text")
    private WebElement confirmationText;

    @FindBy(css = ".pony_express")
    private WebElement confirmationImage;

    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;

    /* ------------------------------------------------------------------ */
    /*  State queries                                                       */
    /* ------------------------------------------------------------------ */

    /** True when the confirmation page has fully loaded. */
    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(confirmationHeader);
            return getCurrentUrl().contains("checkout-complete");
        } catch (Exception e) {
            log.warn("Confirmation page did not load: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns the main confirmation heading.
     * Expected: "Thank you for your order!"
     */
    public String getConfirmationHeader() {
        return getText(confirmationHeader);
    }

    /**
     * Returns the sub-text beneath the heading.
     * Expected to contain dispatch information.
     */
    public String getConfirmationText() {
        return getText(confirmationText);
    }

    /** True if the confirmation image (rocket / pony express) is visible. */
    public boolean isConfirmationImageVisible() {
        try {
            return confirmationImage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Actions                                                             */
    /* ------------------------------------------------------------------ */

    /** Clicks "Back Home" to return to the products list. */
    public InventoryPage backToProducts() {
        click(backToProductsButton);
        log.info("Navigated back to Products page from Order Confirmation.");
        return new InventoryPage();
    }
}
