package com.reservassk.stepdefinitions;

import com.reservassk.pages.CartPage;
import com.reservassk.pages.CategoryPage;
import com.reservassk.pages.HomePage;
import com.reservassk.pages.SearchResultsPage;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.thucydides.core.annotations.Managed;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class OpenCartStepDefinitions {

    @Managed(driver = "chrome")
    WebDriver driver;

    private HomePage homePage;
    private SearchResultsPage searchResultsPage;
    private CategoryPage categoryPage;
    private CartPage cartPage;

    @Before
    public void setUpPages() {
        homePage = new HomePage(driver);
        searchResultsPage = new SearchResultsPage(driver);
        categoryPage = new CategoryPage(driver);
        cartPage = new CartPage(driver);
    }

    @Dado("que el usuario abre la home de OpenCart")
    public void queElUsuarioAbreLaHomeDeOpenCart() {
        homePage.open();
    }

    @Entonces("la home debe cargar correctamente")
    public void laHomeDebeCargarCorrectamente() {
        Assert.assertTrue("La home de OpenCart no cargo correctamente.", homePage.isLoaded());
        Assert.assertTrue("El menu principal no esta visible.", homePage.isTopMenuVisible());
    }

    @Cuando("el usuario busca el producto {string}")
    public void elUsuarioBuscaElProducto(String producto) {
        homePage.searchFor(producto);
    }

    @Entonces("la busqueda debe mostrar resultados para {string}")
    public void laBusquedaDebeMostrarResultadosPara(String producto) {
        Assert.assertTrue("La busqueda no devolvio resultados.", searchResultsPage.hasAnyResult());
        Assert.assertTrue("No se encontro el producto esperado en resultados.", searchResultsPage.containsProduct(producto));
    }

    @Cuando("el usuario abre la categoria Laptops and Notebooks")
    public void elUsuarioAbreLaCategoriaLaptopsAndNotebooks() {
        homePage.openLaptopsAndNotebooksCategory();
    }

    @Entonces("la categoria Laptops and Notebooks debe abrir correctamente")
    public void laCategoriaLaptopsAndNotebooksDebeAbrirCorrectamente() {
        Assert.assertTrue("No se abrio la categoria Laptops & Notebooks.", categoryPage.isLaptopsCategoryLoaded());
    }

    @Cuando("el usuario agrega el producto destacado {string} al carrito")
    public void elUsuarioAgregaElProductoDestacadoAlCarrito(String producto) {
        homePage.addFeaturedProductToCart(producto);
    }

    @Entonces("debe mostrarse una alerta de exito")
    public void debeMostrarseUnaAlertaDeExito() {
        Assert.assertTrue("No aparecio alerta de exito al agregar producto.", homePage.successAlertContains("Success"));
    }

    @Entonces("el carrito debe contener el producto {string}")
    public void elCarritoDebeContenerElProducto(String producto) {
        homePage.goToShoppingCartFromSuccessAlert();
        Assert.assertTrue("No se abrio la pagina de carrito.", cartPage.isLoaded());
        Assert.assertTrue("El producto no esta en el carrito.", cartPage.hasProduct(producto));
    }
}
