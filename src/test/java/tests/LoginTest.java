package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;

/**
 * LoginTest — validates the login page behaviour.
 *
 * Groups: smoke
 */
public class LoginTest extends BaseTest {

    /* ------------------------------------------------------------------ */
    /*  Positive tests                                                      */
    /* ------------------------------------------------------------------ */

    @Test(
        groups     = {"smoke"},
        description = "Valid credentials should land the user on the Products page"
    )
    public void validLoginShouldOpenInventoryPage() {
        LoginPage loginPage = new LoginPage().open();

        InventoryPage inventoryPage = loginPage.loginWithValidCredentials();

        Assert.assertTrue(inventoryPage.isLoaded(),
                "Expected inventory page to load after valid login.");
        Assert.assertEquals(inventoryPage.getPageHeading(), "Products",
                "Page heading should be 'Products'.");
    }

    @Test(
        groups     = {"smoke"},
        description = "Login page title should be 'Swag Labs'"
    )
    public void loginPageShouldHaveCorrectTitle() {
        LoginPage loginPage = new LoginPage().open();

        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Login page should be displayed.");
    }

    /* ------------------------------------------------------------------ */
    /*  Negative tests                                                      */
    /* ------------------------------------------------------------------ */

    @Test(
        groups     = {"smoke"},
        description = "Locked-out user should see an error message"
    )
    public void lockedOutUserShouldSeeError() {
        LoginPage loginPage = new LoginPage().open();
        loginPage.loginAs("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error banner should appear for locked_out_user.");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Error message should mention 'locked out'.");
    }

    @Test(
        groups     = {"smoke"},
        description = "Invalid password should show an error banner"
    )
    public void invalidPasswordShouldShowError() {
        LoginPage loginPage = new LoginPage().open();
        loginPage.loginAs("standard_user", "wrong_password");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error banner should appear for wrong password.");
        Assert.assertFalse(loginPage.getErrorMessage().isEmpty(),
                "Error message should not be empty.");
    }

    @Test(
        groups     = {"smoke"},
        description = "Empty username should display validation error"
    )
    public void emptyUsernameShouldShowError() {
        LoginPage loginPage = new LoginPage().open();
        loginPage.loginAs("", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error banner should appear for empty username.");
    }

    @Test(
        groups     = {"smoke"},
        description = "Empty password should display validation error"
    )
    public void emptyPasswordShouldShowError() {
        LoginPage loginPage = new LoginPage().open();
        loginPage.loginAs("standard_user", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error banner should appear for empty password.");
    }

    /* ------------------------------------------------------------------ */
    /*  Session / logout tests                                              */
    /* ------------------------------------------------------------------ */

    @Test(
        groups     = {"smoke"},
        description = "User should be able to logout and return to login page"
    )
    public void logoutShouldReturnToLoginPage() {
        LoginPage loginPage = new LoginPage().open();

        InventoryPage inventoryPage = loginPage.loginWithValidCredentials();
        Assert.assertTrue(inventoryPage.isLoaded(), "Should be on inventory after login.");

        LoginPage returnedLoginPage = inventoryPage.logout();
        Assert.assertTrue(returnedLoginPage.isOnLoginPage(),
                "Should be back on login page after logout.");
    }
}
