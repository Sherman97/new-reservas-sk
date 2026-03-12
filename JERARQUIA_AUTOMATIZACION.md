# Jerarquia de Automatizacion (OpenCart)

Este proyecto automatiza la plataforma:
`http://opencart.abstracta.us/index.php?route=common/home`

El objetivo funcional es cubrir flujos de negocio visibles:
- cargar home,
- buscar productos,
- navegar por categorias,
- agregar al carrito y validar resultado.

## 1) Carpeta base: `automation-selenium-pom`

Aqui nace y se ejecuta toda la automatizacion.

Que hacemos aqui:
- correr la suite (`mvn clean test`),
- mantener configuracion tecnica global,
- centralizar codigo de pruebas y reportes.

## 2) `pom.xml` (contrato tecnico)

Aqui se define con que herramientas corre la automatizacion.

Que hacemos aqui:
- declarar Selenium, TestNG y WebDriverManager,
- configurar el motor de ejecucion (`surefire`),
- fijar version de Java del proyecto.

## 3) `testng.xml` (plan de ejecucion)

Aqui se decide que pruebas corren y en que suite.

Que hacemos aqui:
- registrar clases de prueba (POM y espagueti),
- ordenar la corrida para demo o regresion,
- separar escenarios por objetivo.

## 4) `src/test/java/com/reservassk/base` (base comun)

Aqui vive la infraestructura transversal.

Que hacemos aqui:
- abrir/cerrar navegador,
- aplicar timeouts y setup/teardown compartido.

Archivo clave:
- `BaseTest.java`

## 5) `src/test/java/com/reservassk/pages` (POM del dominio)

Aqui se modela el lenguaje ubicuo de la plataforma OpenCart por pantallas.
Este es el lugar donde definimos "variables de automatizacion" de interfaz:
locators, componentes y acciones de cada vista.

Que hacemos aqui:
- encapsular localizadores de UI,
- construir funciones de negocio por pagina.

Ejemplos actuales:
- `HomePage`: abrir home, buscar, navegar categoria, agregar a carrito.
- `SearchResultsPage`: validar resultados.
- `CategoryPage`: validar categoria cargada.
- `CartPage`: validar contenido del carrito.

## 6) `src/test/java/com/reservassk/tests` (casos de prueba ejecutables)

Aqui se implementan los casos de prueba del negocio.
Si hablas en Gherkin (Given/When/Then), aqui se traducen a codigo ejecutable.

Que hacemos aqui:
- crear escenarios automatizados por flujo,
- validar criterios de aceptacion.

Ejemplos actuales:
- `OpenCartPomTest`: version limpia y mantenible.
- `OpenCartSpaghettiTest`: version sin POM para comparar enfoque.

## 7) `target/` (evidencia de ejecucion)

Es salida automatica de Maven. No se edita.

Que hacemos aqui:
- revisar resultados y trazabilidad de la corrida.

Ruta clave:
- `target/surefire-reports` (HTML/XML de pruebas).

## Flujo recomendado del equipo

1. Definir el flujo de negocio que se va a automatizar (en lenguaje de usuario).
2. Modelar pagina y acciones en `pages/`.
3. Implementar el caso en `tests/` usando Given/When/Then como guia de lectura.
4. Ejecutar por `testng.xml`.
5. Validar evidencia en `target/surefire-reports`.
