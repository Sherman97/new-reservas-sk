# Jerarquia de Automatizacion (OpenCart - Serenity + Gherkin)

Este proyecto automatiza:
`http://opencart.abstracta.us/index.php?route=common/home`

El lenguaje ubicuo del proyecto gira sobre estos flujos:
- abrir home,
- buscar producto,
- navegar categoria,
- agregar al carrito,
- comparar enfoque limpio (POM) vs enfoque espagueti.

## 1) Carpeta base: `automation-selenium`

Aqui vive toda la automatizacion.

Que hacemos aqui:
- ejecutar pruebas (`gradle clean test aggregate`),
- mantener dependencias y configuracion global,
- generar reportes Serenity.

## 2) `build.gradle` (contrato de ejecucion)

Aqui se define el motor tecnico.

Que hacemos aqui:
- declarar Serenity Core, Serenity JUnit y Serenity Cucumber,
- configurar tarea `test` con JUnit,
- ejecutar `aggregate` para construir reporte Serenity.

Archivo de soporte:
- `settings.gradle` (nombre del modulo Gradle).

## 3) `serenity.properties` (politica de corrida)

Aqui se define el comportamiento base del framework.

Que hacemos aqui:
- seleccionar navegador (`webdriver.driver=chrome`),
- habilitar descarga automatica del driver,
- definir estrategia de screenshots y nombre del proyecto.

## 4) `src/test/resources/features/opencart` (negocio en Gherkin)

Aqui se escriben los casos en lenguaje del usuario.

Que hacemos aqui:
- documentar escenarios con `Dado/Cuando/Entonces`,
- separar features por capacidad del dominio.

Features actuales:
- `opencart_home.feature`
- `opencart_busqueda.feature`
- `opencart_categoria.feature`
- `opencart_carrito.feature`
- `opencart_spaghetti.feature` (flujo corto anti-patron)

## 5) `src/test/java/com/reservassk/stepdefinitions` (traduccion ejecutable)

Aqui se implementa la union entre Gherkin y Selenium.

Que hacemos aqui:
- mapear frases de negocio a pasos ejecutables,
- orquestar acciones y validaciones.

Clases actuales:
- `OpenCartStepDefinitions` (flujo limpio apoyado en Page Objects)
- `OpenCartSpaghettiStepDefinitions` (flujo corto espagueti sin POM)

## 6) `src/test/java/com/reservassk/pages` (POM del dominio)

Aqui se concentra la capa mantenible de UI.

Que hacemos aqui:
- encapsular localizadores,
- exponer acciones por pantalla.

Pages actuales:
- `HomePage`
- `SearchResultsPage`
- `CategoryPage`
- `CartPage`

## 7) `src/test/java/com/reservassk/runners` (orquestador)

Aqui se define el punto de entrada de la suite.

Que hacemos aqui:
- ejecutar features con Serenity + Cucumber.

Runner actual:
- `OpenCartCucumberTest`

## 8) `build/` (evidencia)

Carpeta autogenerada, no se edita.

Rutas utiles:
- `build/reports/serenity`

## Flujo recomendado del equipo

1. Escribir o ajustar escenario en `features/`.
2. Implementar/ajustar pasos en `stepdefinitions/`.
3. Reusar o extender acciones en `pages/`.
4. Ejecutar `gradle clean test aggregate`.
5. Revisar evidencia en reporte Serenity.
