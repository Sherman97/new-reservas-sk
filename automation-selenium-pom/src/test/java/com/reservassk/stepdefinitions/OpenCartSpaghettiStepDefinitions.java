package com.reservassk.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import net.thucydides.core.annotations.Managed;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OpenCartSpaghettiStepDefinitions {

    @Managed(driver = "chrome")
    WebDriver driver;

    private boolean flujoExitoso;

    @Cuando("el usuario ejecuta el flujo espagueti para agregar {string}")
    public void elUsuarioEjecutaElFlujoEspaguetiParaAgregar(String producto) {
        flujoExitoso = false;
        driver.manage().window().maximize();
        driver.get("http://opencart.abstracta.us/index.php?route=common/home");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement productCard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'product-thumb')][.//h4/a[normalize-space()='" + producto + "']]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", productCard);

        WebElement addButton = productCard.findElement(By.xpath(".//button[contains(@onclick,'cart.add')]"));
        wait.until(ExpectedConditions.elementToBeClickable(addButton)).click();

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-success, .alert.alert-success")));
        flujoExitoso = alert.getText().contains("Success");
    }

    @Entonces("el flujo espagueti termina con exito")
    public void elFlujoEspaguetiTerminaConExito() {
        Assert.assertTrue("El flujo espagueti no termino en exito.", flujoExitoso);
    }
}
