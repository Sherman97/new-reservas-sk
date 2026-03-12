package com.reservassk.tests;

import com.reservassk.base.BaseTest;
import com.reservassk.pages.CartPage;
import com.reservassk.pages.CategoryPage;
import com.reservassk.pages.HomePage;
import com.reservassk.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class OpenCartPomTest extends BaseTest {

    @Test
    public void homeDebeCargarYMostrarMenuPrincipal() {
        HomePage homePage = new HomePage(driver);

        homePage.open();

        Assert.assertTrue(homePage.isLoaded(), "La home de OpenCart no cargo correctamente.");
        Assert.assertTrue(homePage.isTopMenuVisible(), "El menu principal no esta visible.");
    }

    @Test
    public void busquedaDebeMostrarProductoEsperado() {
        HomePage homePage = new HomePage(driver);
        SearchResultsPage resultsPage = new SearchResultsPage(driver);

        homePage.open();
        homePage.searchFor("iPhone");

        Assert.assertTrue(resultsPage.hasAnyResult(), "La busqueda no devolvio resultados.");
        Assert.assertTrue(resultsPage.containsProduct("iPhone"), "No se encontro el producto iPhone en los resultados.");
    }

    @Test
    public void categoriaLaptopsDebeAbrirCorrectamente() {
        HomePage homePage = new HomePage(driver);
        CategoryPage categoryPage = new CategoryPage(driver);

        homePage.open();
        homePage.openLaptopsAndNotebooksCategory();

        Assert.assertTrue(categoryPage.isLaptopsCategoryLoaded(), "No se abrio la categoria Laptops & Notebooks.");
    }

    @Test
    public void agregarProductoDestacadoDebeReflejarseEnCarrito() {
        HomePage homePage = new HomePage(driver);
        CartPage cartPage = new CartPage(driver);

        homePage.open();
        homePage.addFeaturedProductToCart("iPhone");

        Assert.assertTrue(homePage.successAlertContains("Success"), "No aparecio alerta de exito al agregar producto.");

        homePage.goToShoppingCartFromSuccessAlert();
        Assert.assertTrue(cartPage.isLoaded(), "No se abrio la pagina de carrito.");
        Assert.assertTrue(cartPage.hasProduct("iPhone"), "El producto iPhone no esta en el carrito.");
    }
}
