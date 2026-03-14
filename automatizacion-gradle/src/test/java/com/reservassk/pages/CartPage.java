package com.reservassk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("route=checkout/cart"),
                    ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#content h1"), "Shopping Cart")
            ));
            return driver.getCurrentUrl().contains("route=checkout/cart")
                    || !driver.findElements(By.cssSelector("#content h1")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasProduct(String productName) {
        List<WebElement> products = driver.findElements(By.cssSelector(".table-responsive tbody tr td:nth-child(2) a"));
        return products.stream().anyMatch(p -> p.getText().trim().equalsIgnoreCase(productName));
    }
}
