package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CartPage — Page Object for /cart.html.
 */
public class CartPage extends BasePage {

    /* ------------------------------------------------------------------ */
    /*  Locators                                                            */
    /* ------------------------------------------------------------------ */

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    /* ------------------------------------------------------------------ */
    /*  State queries                                                       */
    /* ------------------------------------------------------------------ */

    /** True when the cart page URL is active. */
    public boolean isLoaded() {
    	try {
            WaitUtils.waitForVisible(pageTitle);
            WaitUtils.waitForUrlContains("cart.html");
            return getCurrentUrl().contains("cart.html");
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns all product names currently in the cart. */
    public List<String> getCartProductNames() {
        return cartItems.stream()
                .map(item -> item.findElement(By.className("inventory_item_name")).getText())
                .collect(Collectors.toList());
    }

    /** Returns the number of distinct line-items in the cart. */
    public int getCartItemCount() {
        return cartItems.size();
    }

    /** True if a product with the given name is in the cart. */
    public boolean isProductInCart(String productName) {
        return getCartProductNames().stream()
                .anyMatch(name -> name.equalsIgnoreCase(productName));
    }

    /* ------------------------------------------------------------------ */
    /*  Actions                                                             */
    /* ------------------------------------------------------------------ */

    /** Proceeds to checkout. */
    public CheckoutPage proceedToCheckout() {
    	WaitUtils.waitForVisible(checkoutButton);
        WaitUtils.waitForClickable(checkoutButton);
        click(checkoutButton);
        log.info("Clicked Checkout button.");
        return new CheckoutPage();

    }

    /** Returns to the inventory page without checking out. */
    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage();
    }

    /** Removes a specific product from the cart by name. */
    public CartPage removeProduct(String productName) {
        cartItems.stream()
                .filter(item -> item.findElement(By.className("inventory_item_name"))
                        .getText().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart: " + productName))
                .findElement(By.cssSelector("button[data-test^='remove']"))
                .click();
        log.info("Removed product from cart: {}", productName);
        return this;
    }
}
