package com.reservassk.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CategoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isLaptopsCategoryLoaded() {
        wait.until(ExpectedConditions.urlContains("route=product/category"));

        List<WebElement> titles = driver.findElements(By.cssSelector("#content h1, #content h2"));
        if (titles.isEmpty()) {
            return driver.getCurrentUrl().contains("path=18");
        }

        String heading = titles.get(0).getText().toLowerCase();
        return heading.contains("laptop") || heading.contains("notebook");
    }
}
