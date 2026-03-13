# Automatizacion Selenium - OpenCart (Serenity + Gherkin)

Proyecto de automatizacion UI en Java con **Serenity BDD + Cucumber (Gherkin)** usando Page Objects sobre OpenCart demo.

Sitio objetivo:
- `http://opencart.abstracta.us/index.php?route=common/home`

## Stack actual

- Java 17
- Gradle 8+
- Serenity BDD 3.9.8
- Serenity Cucumber 6
- Chrome

## Estructura principal

- `src/test/java/com/reservassk/pages/`
  - `HomePage`, `SearchResultsPage`, `CategoryPage`, `CartPage`.
  - Encapsulan localizadores y acciones por pagina.
- `src/test/java/com/reservassk/runners/OpenCartCucumberTest.java`
  - Runner de Serenity para ejecutar features.
- `src/test/java/com/reservassk/stepdefinitions/OpenCartStepDefinitions.java`
  - Definicion de pasos Gherkin en espanol.
- `src/test/resources/features/opencart/*.feature`
  - Casos en lenguaje Gherkin.
- `serenity.properties`
  - Configuracion de navegador y reporte Serenity.

## Casos automatizados implementados

Total: **5 escenarios Gherkin**

1. Carga correcta de la home.
2. Busqueda de iPhone.
3. Apertura de categoria Laptops and Notebooks.
4. Agregar producto destacado al carrito.
5. Flujo corto espagueti (sin Page Objects) para agregar iPhone.

## Ejecucion

Desde la carpeta `automation-selenium`:

```powershell
gradle clean test aggregate
```

Con Gradle Wrapper (recomendado cuando lo generes):

```powershell
.\gradlew clean test aggregate
```

Reporte Serenity generado en:
- `build/reports/serenity`

## Nota tecnica sobre CDP

Es posible ver advertencias como:
- `Unable to find version of CDP to use for 145...`

En este proyecto no se usan APIs DevTools directamente, por lo que esa advertencia suele ser informativa y no bloqueante.
