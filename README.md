# Automatizacion Selenium - OpenCart (POM vs Spaghetti)

Proyecto de automatizacion UI en Java para demostrar el patron **Page Object Model (POM)** frente a un enfoque **espagueti** sobre OpenCart demo.

Sitio objetivo:
- `http://opencart.abstracta.us/index.php?route=common/home`

## Stack actual

- Java 17
- Maven 3.9+
- Selenium 4.29.0
- TestNG 7.11.0
- WebDriverManager 5.9.3
- Chrome + ChromeDriver administrado por WebDriverManager

## Estructura principal

- `src/test/java/com/reservassk/base/BaseTest.java`
  - Configuracion comun de driver (setup/teardown).
- `src/test/java/com/reservassk/pages/`
  - `HomePage`, `SearchResultsPage`, `CategoryPage`, `CartPage`.
  - Encapsulan localizadores y acciones por pagina.
- `src/test/java/com/reservassk/tests/OpenCartPomTest.java`
  - Suite limpia con POM.
- `src/test/java/com/reservassk/tests/OpenCartSpaghettiTest.java`
  - Ejemplo anti-patron (todo en un solo test).
- `testng.xml`
  - Suite que ejecuta ambos enfoques.

## Casos automatizados implementados

Total: **5 casos**

Casos POM (`OpenCartPomTest`):
1. `homeDebeCargarYMostrarMenuPrincipal`
2. `busquedaDebeMostrarProductoEsperado`
3. `categoriaLaptopsDebeAbrirCorrectamente`
4. `agregarProductoDestacadoDebeReflejarseEnCarrito`

Caso espagueti (`OpenCartSpaghettiTest`):
1. `agregarProductoDesdeHome_spaghetti`

## Ejecucion

Desde la carpeta `automation-selenium-pom`:

```powershell
mvn clean test
```

Para ejecutar solo la suite POM:

```powershell
mvn -Dtest=OpenCartPomTest test
```

Para ejecutar solo el test espagueti:

```powershell
mvn -Dtest=OpenCartSpaghettiTest test
```

## Nota tecnica sobre CDP

Es posible ver advertencias como:
- `Unable to find version of CDP to use for 145...`

En este proyecto no se usan APIs DevTools directamente, por lo que esa advertencia suele ser informativa y no bloqueante.
