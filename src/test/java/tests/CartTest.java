package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.InventoryPage;
import pages.LoginPage;

import java.util.List;

/**
 * CartTest — validates cart behaviour: item presence, removal, persistence.
 *
 * Groups: cart
 */
public class CartTest extends BaseTest {

    @Test(
        groups     = {"cart"},
        description = "Products added on inventory page should appear in the cart"
    )
    public void addedProductsShouldAppearInCart() {
        List<String> productsToAdd = config.getProductsToAdd();

        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        for (String product : productsToAdd) {
            inventoryPage.addProductToCart(product);
        }

        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertTrue(cartPage.isLoaded(), "Cart page should be loaded.");

        for (String product : productsToAdd) {
            Assert.assertTrue(cartPage.isProductInCart(product),
                    "Expected product in cart: " + product);
        }
    }

    @Test(
        groups     = {"cart"},
        description = "Cart should show correct item count"
    )
    public void cartShouldShowCorrectItemCount() {
        List<String> productsToAdd = config.getProductsToAdd();

        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        for (String product : productsToAdd) {
            inventoryPage.addProductToCart(product);
        }

        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), productsToAdd.size(),
                "Cart item count should match number of added products.");
    }

    @Test(
        groups     = {"cart"},
        description = "Removing a product from cart should decrease item count"
    )
    public void removingProductFromCartShouldDecreaseCount() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        inventoryPage
                .addProductToCart("Sauce Labs Backpack")
                .addProductToCart("Sauce Labs Bike Light");

        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Should have 2 items.");

        cartPage.removeProduct("Sauce Labs Backpack");
        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Should have 1 item after removal.");
        Assert.assertFalse(cartPage.isProductInCart("Sauce Labs Backpack"),
                "Removed product should not be in cart.");
    }

    @Test(
        groups     = {"cart"},
        description = "Continue shopping from cart should return to inventory"
    )
    public void continueShouldReturnToInventory() {
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginWithValidCredentials();

        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.goToCart();

        InventoryPage returnedInventory = cartPage.continueShopping();
        Assert.assertTrue(returnedInventory.isLoaded(),
                "Should return to inventory page after Continue Shopping.");
    }
}
