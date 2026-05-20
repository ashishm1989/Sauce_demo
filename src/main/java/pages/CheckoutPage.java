package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;
import utils.WaitUtils;

/**
 * CheckoutPage — covers both checkout steps:
 *  Step 1 (/checkout-step-one.html)  — customer info form
 *  Step 2 (/checkout-step-two.html)  — order overview
 */
public class CheckoutPage extends BasePage {

    private static final ConfigReader config = ConfigReader.getInstance();

    /* ------------------------------------------------------------------ */
    /*  Step-One locators                                                   */
    /* ------------------------------------------------------------------ */

    @FindBy(id = "first-name")
    private WebElement firstNameInput;

    @FindBy(id = "last-name")
    private WebElement lastNameInput;

    @FindBy(id = "postal-code")
    private WebElement zipCodeInput;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    /* ------------------------------------------------------------------ */
    /*  Step-Two locators                                                   */
    /* ------------------------------------------------------------------ */

    @FindBy(className = "summary_total_label")
    private WebElement totalLabel;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    /* ------------------------------------------------------------------ */
    /*  State queries                                                       */
    /* ------------------------------------------------------------------ */

    public boolean isStepOneLoaded() {
        try {
            WaitUtils.waitForVisible(firstNameInput);
            return getCurrentUrl().contains("checkout-step-one");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isStepTwoLoaded() {
        try {
            WaitUtils.waitForVisible(finishButton);
            return getCurrentUrl().contains("checkout-step-two");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return isErrorDisplayed() ? getText(errorMessage) : "";
    }

    /** Returns the full total string, e.g. "Total: $43.18". */
    public String getTotalPrice() {
        return getText(totalLabel);
    }

    /* ------------------------------------------------------------------ */
    /*  Actions                                                             */
    /* ------------------------------------------------------------------ */

    /** Fills in the customer info form. */
    public CheckoutPage enterShippingInfo(String firstName, String lastName, String zipCode) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(zipCodeInput, zipCode);
        log.info("Entered checkout info: {} {}, {}", firstName, lastName, zipCode);
        return this;
    }

    /** Fills shipping info using config-file values. */
    public CheckoutPage enterShippingInfoFromConfig() {
        return enterShippingInfo(
                config.getCheckoutFirstName(),
                config.getCheckoutLastName(),
                config.getCheckoutZip()
        );
    }

    /** Clicks Continue to move from step one to step two. */
    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    /** Clicks Finish on the overview page. */
    public OrderConfirmationPage clickFinish() {
        click(finishButton);
        log.info("Clicked Finish — order placed.");
        return new OrderConfirmationPage();
    }

    /** Cancels checkout and returns to inventory. */
    public InventoryPage cancel() {
        click(cancelButton);
        return new InventoryPage();
    }

    /**
     * Complete Step 1 in a single call:
     * fill info → continue to step 2.
     */
    public CheckoutPage completeStepOne(String firstName, String lastName, String zip) {
        enterShippingInfo(firstName, lastName, zip);
        clickContinue();
        return this;
    }
}
