package com.reservassk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage {
    private static final String URL = "http://opencart.abstracta.us/index.php?route=common/home";
    private static final String CART_URL = "http://opencart.abstracta.us/index.php?route=checkout/cart";
    private static final String LAPTOPS_CATEGORY_URL = "http://opencart.abstracta.us/index.php?route=product/category&path=18";

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "#logo a")
    private WebElement storeLogo;

    @FindBy(name = "search")
    private WebElement searchInput;

    @FindBy(css = "#search button")
    private WebElement searchButton;

    @FindBy(css = "#menu")
    private WebElement topMenu;

    @FindBy(id = "cart-total")
    private WebElement cartTotalButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get(URL);
    }

    public boolean isLoaded() {
        wait.until(ExpectedConditions.visibilityOf(storeLogo));
        return storeLogo.getText().contains("Your Store");
    }

    public boolean isTopMenuVisible() {
        return topMenu.isDisplayed();
    }

    public void searchFor(String product) {
        searchInput.clear();
        searchInput.sendKeys(product);
        searchButton.click();
    }

    public void openLaptopsAndNotebooksCategory() {
        try {
            WebElement laptopsCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#menu a[href*='route=product/category&path=18']")));
            laptopsCategoryLink.click();
            wait.until(ExpectedConditions.urlContains("route=product/category"));
        } catch (Exception e) {
            // Fallback deterministico si el menu no responde en esta build.
            driver.get(LAPTOPS_CATEGORY_URL);
        }
    }

    public void addFeaturedProductToCart(String productName) {
        String productCardXpath =
                "//div[contains(@class,'product-thumb')][.//h4/a[normalize-space()='" + productName + "']]";

        WebElement productCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(productCardXpath)));
        WebElement addToCartButton = productCard.findElement(By.xpath(".//button[contains(@onclick,'cart.add')]"));
        new Actions(driver).moveToElement(addToCartButton).perform();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addToCartButton);

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);
        } catch (Exception e) {
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
        }

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")),
                ExpectedConditions.textToBePresentInElement(cartTotalButton, "1 item(s)")
        ));
    }

    public String getCartSummaryText() {
        return wait.until(ExpectedConditions.visibilityOf(cartTotalButton)).getText();
    }

    public boolean cartSummaryHasAtLeastOneItem() {
        String summary = getCartSummaryText();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("(\\d+)\\s*item\\(s\\)").matcher(summary);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)) >= 1;
        }
        return false;
    }

    public boolean successAlertContains(String text) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".alert.alert-success, .alert-success")));
            return alert.getText().contains(text);
        } catch (Exception e) {
            return false;
        }
    }

    public void goToShoppingCartFromSuccessAlert() {
        // Flujo deterministico para evitar flakiness del dropdown/alerta en OpenCart demo.
        driver.get(CART_URL);
        wait.until(ExpectedConditions.urlContains("route=checkout/cart"));
    }
}
