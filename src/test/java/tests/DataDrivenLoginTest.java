package tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.CsvDataProvider;

/**
 * DataDrivenLoginTest — exercises the login form with multiple credential
 * combinations loaded from {@code testdata/login_data.csv}.
 *
 * CSV columns: username, password, expectedResult, description
 *   expectedResult = "success" → user lands on inventory
 *   expectedResult = "failure" → error banner should appear
 *
 * Groups: datadriven
 */
public class DataDrivenLoginTest extends BaseTest {

    /* ------------------------------------------------------------------ */
    /*  DataProvider                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Reads login test data from the CSV file.
     *
     * @return 2D array where each row is {username, password, expectedResult, description}
     */
    @DataProvider(name = "loginData", parallel = false)
    public Object[][] loginDataProvider() {
        return CsvDataProvider.loadCsv("testdata/login_data.csv");
    }

    /* ------------------------------------------------------------------ */
    /*  Test                                                                */
    /* ------------------------------------------------------------------ */

    @Test(
        dataProvider = "loginData",
        groups       = {"datadriven"},
        description  = "Data-driven login — validates success and failure scenarios from CSV"
    )
    public void loginWithVariousCredentials(
            String username,
            String password,
            String expectedResult,
            String description)
    {
        log.info("Running: [{}] username='{}', expectedResult='{}'",
                description, username, expectedResult);

        LoginPage loginPage = new LoginPage().open();

        if ("success".equalsIgnoreCase(expectedResult)) {
            // ── Positive path ────────────────────────────────────────────
            InventoryPage inventoryPage = loginPage.loginAs(username, password);

            Assert.assertTrue(inventoryPage.isLoaded(),
                    String.format("[%s] Login should succeed for user '%s', "
                                  + "but inventory page did not load.", description, username));

        } else {
            // ── Negative path ────────────────────────────────────────────
            loginPage.loginAs(username, password);

            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    String.format("[%s] Login should FAIL for user '%s', "
                                  + "but no error banner appeared.", description, username));

            Assert.assertFalse(loginPage.getErrorMessage().isEmpty(),
                    "Error message text should not be empty.");
        }
    }
}
