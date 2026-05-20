package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import java.util.List;

/**
 * E2ECheckoutTest — full happy-path end-to-end journey:
 *
 *  1. Open saucedemo.com
 *  2. Login with valid credentials
 *  3. Verify inventory page loaded
 *  4. Add 2 products from config to the cart
 *  5. Navigate to the cart
 *  6. Verify selected items are present
 *  7. Proceed through checkout step-one (customer info)
 *  8. Proceed through checkout step-two (order summary)
 *  9. Finish — verify order confirmation message
 *
 * Groups: e2e
 */
public class E2ECheckoutTest extends BaseTest {

    @Test(
        groups     = {"e2e"},
        description = "Complete end-to-end checkout journey should show order confirmation"
    )
    public void completeCheckoutShouldShowConfirmation() {

        // ── Step 1: Open login page ──────────────────────────────────────
        log.info("Step 1: Open login page");
        LoginPage loginPage = new LoginPage().open();
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "Login page should be visible.");

        // ── Step 2: Login ────────────────────────────────────────────────
        log.info("Step 2: Login with valid credentials");
        InventoryPage inventoryPage = loginPage.loginWithValidCredentials();

        // ── Step 3: Verify inventory ─────────────────────────────────────
        log.info("Step 3: Verify inventory page loaded");
        Assert.assertTrue(inventoryPage.isLoaded(),
                "Inventory page should load after login.");
        Assert.assertEquals(inventoryPage.getPageHeading(), "Products",
                "Heading should be 'Products'.");

        // ── Step 4: Add products to cart ─────────────────────────────────
        log.info("Step 4: Add products to cart");
        List<String> productsToAdd = config.getProductsToAdd();
        Assert.assertFalse(productsToAdd.isEmpty(),
                "Config must specify at least one product to add.");

        for (String product : productsToAdd) {
            inventoryPage.addProductToCart(product);
        }
        Assert.assertEquals(inventoryPage.getCartItemCount(), productsToAdd.size(),
                "Cart badge count should equal number of added products.");

        // ── Step 5: Navigate to cart ─────────────────────────────────────
        log.info("Step 5: Navigate to cart");
        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should load.");

        // ── Step 6: Verify items in cart ─────────────────────────────────
        log.info("Step 6: Verify all added products are in the cart");
        for (String product : productsToAdd) {
            Assert.assertTrue(cartPage.isProductInCart(product),
                    "Product should be present in cart: " + product);
        }
        Assert.assertEquals(cartPage.getCartItemCount(), productsToAdd.size(),
                "Cart should contain exactly the added items.");

        // ── Step 7: Checkout Step 1 — customer info ──────────────────────
        log.info("Step 7: Fill checkout information");
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isStepOneLoaded(),
                "Checkout step-one should load.");

        checkoutPage.completeStepOne(
                config.getCheckoutFirstName(),
                config.getCheckoutLastName(),
                config.getCheckoutZip()
        );

        // ── Step 8: Checkout Step 2 — order summary ───────────────────────
        log.info("Step 8: Review order summary");
        Assert.assertTrue(checkoutPage.isStepTwoLoaded(),
                "Checkout step-two (order overview) should load.");

        String totalPrice = checkoutPage.getTotalPrice();
        Assert.assertFalse(totalPrice.isEmpty(), "Order total should be displayed.");
        log.info("Order total: {}", totalPrice);

        // ── Step 9: Finish — verify confirmation ─────────────────────────
        log.info("Step 9: Complete order and verify confirmation");
        OrderConfirmationPage confirmPage = checkoutPage.clickFinish();

        Assert.assertTrue(confirmPage.isLoaded(),
                "Order confirmation page should load after finishing checkout.");

        String header = confirmPage.getConfirmationHeader();
        Assert.assertEquals(header, "Thank you for your order!",
                "Confirmation header should read 'Thank you for your order!'");

        Assert.assertTrue(confirmPage.isConfirmationImageVisible(),
                "Confirmation image should be visible.");

        log.info("✔ E2E checkout completed successfully. Confirmation: '{}'", header);
    }

    @Test(
        groups     = {"e2e"},
        description = "Checkout with missing first name should show a validation error"
    )
    public void checkoutWithMissingFirstNameShouldShowError() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        inventoryPage.addProductToCart("Sauce Labs Backpack");

        CheckoutPage checkoutPage = inventoryPage.goToCart().proceedToCheckout();
        Assert.assertTrue(checkoutPage.isStepOneLoaded(), "Step-one should load.");

        // Submit with blank first name
        checkoutPage.completeStepOne("", config.getCheckoutLastName(), config.getCheckoutZip());

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
                "Error should appear when first name is blank.");
        Assert.assertTrue(checkoutPage.getErrorMessage().toLowerCase().contains("first name"),
                "Error message should mention 'First Name'.");
    }

    @Test(
        groups     = {"e2e"},
        description = "Back-to-products from confirmation should reload inventory"
    )
    public void backToProductsFromConfirmationShouldReloadInventory() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        inventoryPage.addProductToCart("Sauce Labs Backpack");

        OrderConfirmationPage confirmPage = inventoryPage
                .goToCart()
                .proceedToCheckout()
                .completeStepOne(
                        config.getCheckoutFirstName(),
                        config.getCheckoutLastName(),
                        config.getCheckoutZip())
                .clickFinish();

        Assert.assertTrue(confirmPage.isLoaded(), "Confirmation page should load.");

        InventoryPage returnedInventory = confirmPage.backToProducts();
        Assert.assertTrue(returnedInventory.isLoaded(),
                "Should navigate back to inventory page.");
        Assert.assertEquals(returnedInventory.getCartItemCount(), 0,
                "Cart should be empty after order completion.");
    }
}
