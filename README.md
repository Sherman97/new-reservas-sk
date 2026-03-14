# new-reservas-sk

Proyecto de automatizacion UI con Serenity BDD + Cucumber (Gherkin) para OpenCart.

Sitio objetivo:
- `http://opencart.abstracta.us/index.php?route=common/home`

## Estructura actual

- `automatizacion-gradle/`: codigo de automatizacion, Gradle wrapper, configuraciones y reportes.
- `OLD-HU/`: historias de usuario originales.
- `REFACTOR-HU/`: historias de usuario refinadas/refactorizadas.
- Archivos de documentacion en raiz:
- `BUSINESS_CONTEXT.md`
- `TEST_CASES_AI.md`
- `USER_STORIES_REFINEMENT.md`
- `Page-Object-Model-POM-en-Automatizacion-de-Pruebas.pptx`

## Stack

- Java 17
- Gradle
- Serenity BDD
- Cucumber
- Chrome

## Ejecucion

Desde la raiz del repositorio:

```powershell
cd .\automatizacion-gradle
.\gradlew clean test aggregate
```

Reporte Serenity:
- `automatizacion-gradle/build/reports/serenity`

## Nota de versionamiento

El `.gitignore` se mantiene en la raiz del repositorio para controlar correctamente los archivos ignorados de todo el proyecto.
