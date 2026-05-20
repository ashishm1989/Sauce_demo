package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.ConfigReader;
import utils.WaitUtils;

/**
 * LoginPage — Page Object for https://www.saucedemo.com/ (login screen).
 *
 * Encapsulates all locators and interactions on the login page, keeping
 * tests clean and free of low-level Selenium calls.
 */
public class LoginPage extends BasePage {

    private static final ConfigReader config = ConfigReader.getInstance();

    /* ------------------------------------------------------------------ */
    /*  Locators                                                            */
    /* ------------------------------------------------------------------ */

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(css = ".login_logo")
    private WebElement loginLogo;

    /* ------------------------------------------------------------------ */
    /*  Actions                                                             */
    /* ------------------------------------------------------------------ */

    /** Opens the base URL (login page). */
    public LoginPage open() {
        navigateTo(config.getBaseUrl());
        WaitUtils.waitForVisible(loginButton);
        log.info("Login page opened.");
        return this;
    }

    /** Types a username into the username field. */
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    /** Types a password into the password field. */
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    /** Clicks the login button. */
    public LoginPage clickLoginButton() {
        click(loginButton);
        return this;
    }

    /**
     * Full login flow — enters credentials and clicks Login.
     * Returns an InventoryPage (caller should assert it loaded).
     */
    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        log.info("Login attempted for user: {}", username);
        return new InventoryPage();
    }

    /** Convenience overload using configured valid credentials. */
    public InventoryPage loginWithValidCredentials() {
        return loginAs(config.getValidUsername(), config.getValidPassword());
    }

    /* ------------------------------------------------------------------ */
    /*  State queries                                                       */
    /* ------------------------------------------------------------------ */

    /** Returns true when the error banner is displayed. */
    public boolean isErrorDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the text of the error banner, or empty string if absent. */
    public String getErrorMessage() {
        if (isErrorDisplayed()) {
            return getText(errorMessage);
        }
        return "";
    }

    /** Returns true when the login button is present (i.e. still on login page). */
    public boolean isOnLoginPage() {
        try {
            return loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
