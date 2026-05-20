package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private final By pageTitle = By.className("title");
    private final By inventoryItems = By.className("inventory_item_name");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public InventoryTest() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Check if inventory page loaded
     */
    public boolean isLoaded() {
        try {
            WebElement title = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(pageTitle));

            return title.getText().trim().equals("Products");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get heading
     */
    public String getPageHeading() {
        return driver.findElement(pageTitle).getText().trim();
    }

    /**
     * Get all product names
     */
    public List<String> getAllProductNames() {
        return driver.findElements(inventoryItems)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /**
     * Add product to cart
     */
    public InventoryTest addProductToCart(String productName) {
        List<WebElement> products = driver.findElements(By.className("inventory_item"));

        for (WebElement product : products) {
            if (product.getText().contains(productName)) {
                product.findElement(By.tagName("button")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
                break;
            }
        }
        return this;
    }

    /**
     * Remove product from cart
     */
    public InventoryTest removeProductFromCart(String productName) {
        List<WebElement> products = driver.findElements(By.className("inventory_item"));

        for (WebElement product : products) {
            if (product.getText().contains(productName)) {
                product.findElement(By.tagName("button")).click();
                break;
            }
        }
        return this;
    }

    /**
     * Get cart badge count
     */
    public int getCartItemCount() {
        List<WebElement> badges = driver.findElements(cartBadge);

        if (badges.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(badges.get(0).getText().trim());
    }

    /**
     * Logout
     */
    public LoginTest logout() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();

        wait.until(ExpectedConditions.urlContains("saucedemo.com"));

        return new LoginTest();
    }

    /**
     * Open cart
     */
    public CartTest openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        wait.until(ExpectedConditions.urlContains("cart.html"));

        return new CartTest();
    }
}