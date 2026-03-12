package com.reservassk.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class OpenCartSpaghettiTest {

    @Test
    public void agregarProductoDesdeHome_spaghetti() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();
            driver.get("http://opencart.abstracta.us/index.php?route=common/home");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            WebElement productCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'product-thumb')][.//h4/a[normalize-space()='iPhone']]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", productCard);
            WebElement addButton = productCard.findElement(By.xpath(".//button[contains(@onclick,'cart.add')]"));
            wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();

            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".alert-success, .alert.alert-success")));
            Assert.assertTrue(alert.getText().contains("Success"), "No se mostro mensaje de exito.");

            WebElement cartTotal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart-total")));
            wait.until(ExpectedConditions.textToBePresentInElement(cartTotal, "1 item(s)"));
            Assert.assertTrue(cartTotal.getText().contains("1 item(s)"), "No se actualizo el contador del carrito.");
        } finally {
            driver.quit();
        }
    }
}
