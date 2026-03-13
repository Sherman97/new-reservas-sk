package com.reservassk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchResultsPage {
    private final WebDriver driver;

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean hasAnyResult() {
        List<WebElement> products = driver.findElements(By.cssSelector(".product-layout"));
        return !products.isEmpty();
    }

    public boolean containsProduct(String productName) {
        List<WebElement> links = driver.findElements(By.cssSelector(".product-layout .caption h4 a"));
        return links.stream().anyMatch(link -> link.getText().trim().equalsIgnoreCase(productName));
    }
}
