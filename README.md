# Selenium POM Demo (OpenCart: Spaghetti vs POM + Page Factory)

Proyecto base para explicar patron Page Object Model con comparacion directa contra un test "espagueti" sobre OpenCart.

## 1. Que debes instalar en Windows

1. Java 17 (JDK)
2. Maven 3.9.x
3. Google Chrome
4. IntelliJ IDEA (opcional, recomendado)

## 2. Verificar instalacion

En PowerShell:

```powershell
java -version
mvn -v
```

Si `mvn` no aparece, agrega Maven al `PATH`:

- Variable `MAVEN_HOME`: `C:\apache-maven-3.9.x`
- En `Path`: `%MAVEN_HOME%\bin`

Luego abre una nueva terminal y valida otra vez `mvn -v`.

## 3. Ejecutar pruebas

Desde esta carpeta:

```powershell
cd automation-selenium-pom
mvn clean test
```

## 4. Estructura

- `OpenCartSpaghettiTest`: ejemplo anti-patron (locators, acciones y validaciones en una sola clase).
- `HomePage`, `SearchResultsPage`, `CategoryPage`, `CartPage`: capas POM con responsabilidades separadas.
- `OpenCartPomTest`: escenarios limpios usando objetos de pagina.
- `BaseTest`: setup y teardown reutilizable.

## 5. Sitio bajo prueba

- URL: `http://opencart.abstracta.us/index.php?route=common/home`

## 6. Flujos automatizados

- Carga de home y visibilidad de menu principal.
- Busqueda de producto (`iPhone`) y validacion de resultados.
- Navegacion por categoria `Laptops & Notebooks`.
- Agregar producto destacado al carrito y validar contador + carrito.
