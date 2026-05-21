package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * InventoryPage — Page Object for the product listing page
 * (URL: /inventory.html) reached after a successful login.
 */
public class InventoryPage extends BasePage {

    /* ------------------------------------------------------------------ */
    /*  Locators                                                            */
    /* ------------------------------------------------------------------ */

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "inventory_list")
    private WebElement inventoryList;

    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement hamburgerMenu;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    /* ------------------------------------------------------------------ */
    /*  State queries                                                       */
    /* ------------------------------------------------------------------ */

    /** True when the inventory page has fully loaded. */
    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(inventoryList);
            return getCurrentUrl().contains("inventory.html");
        } catch (Exception e) {
            log.warn("Inventory page did not load: {}", e.getMessage());
            return false;
        }
    }

    /** Returns the page heading text (e.g. "Products"). */
    public String getPageHeading() {
        return getText(pageTitle);
    }

    /** Returns names of all products currently listed on the page. */
    public List<String> getAllProductNames() {
        return inventoryItems.stream()
                .map(item -> item.findElement(By.className("inventory_item_name")).getText())
                .collect(Collectors.toList());
    }

    /** Returns the number shown on the cart badge, or 0 if the badge is absent. */
    public int getCartItemCount() {
        try {
            return Integer.parseInt(cartBadge.getText().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Actions                                                             */
    /* ------------------------------------------------------------------ */

    /**
     * Adds a product to the cart by its exact display name.
     *
     * @throws RuntimeException if no product with that name is found
     */
    public InventoryPage addProductToCart(String productName) {
    	WebElement addButton = inventoryItems.stream()
                .filter(item -> item.findElement(By.className("inventory_item_name"))
                        .getText().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + productName))
                .findElement(By.cssSelector("button[data-test^='add-to-cart']"));

        click(addButton);
        WaitUtils.waitForVisible(cartBadge);
        log.info("Added product to cart: {}", productName);
        return this;
    }

    /**
     * Removes a product from the cart while still on the inventory page.
     */
    public InventoryPage removeProductFromCart(String productName) {
        WebElement removeButton = inventoryItems.stream()
                .filter(item -> item.findElement(By.className("inventory_item_name"))
                        .getText().equalsIgnoreCase(productName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + productName))
                .findElement(By.cssSelector("button[data-test^='remove']"));

        click(removeButton);
        log.info("Removed product from cart: {}", productName);
        return this;
    }

    /** Clicks the shopping-cart icon to navigate to the Cart page. */
    public CartPage goToCart() {
        click(cartIcon);
        log.info("Navigated to cart.");
        return new CartPage();
    }

    /** Opens the hamburger menu, then clicks Logout. */
    public LoginPage logout() {
    	 {
    	    click(hamburgerMenu);
    	    WaitUtils.waitForClickable(logoutLink);
    	    click(logoutLink);
    	    log.info("Logged out.");
    	    return new LoginPage();
    	}
    }
}
