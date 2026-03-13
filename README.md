# new-reservas-sk

Proyecto de automatizacion UI con **Serenity BDD + Cucumber (Gherkin)** para OpenCart.

Sitio objetivo:
- `http://opencart.abstracta.us/index.php?route=common/home`

## Estructura

- `new-reservas-sk/` (raiz del repositorio Git)
- `automation-selenium-pom/` (modulo principal de pruebas)
- `OLD-HU/`, `REFACTOR-HU/`, `SKAI-INSTRUCTION/` (documentacion y refinamientos)

## Stack actual

- Java 17
- Gradle 8+
- Serenity BDD 3.9.8
- Serenity Cucumber
- Chrome

## Ejecucion

Desde la raiz del proyecto:

```powershell
.\gradlew :automation-selenium-pom:clean :automation-selenium-pom:test :automation-selenium-pom:aggregate
```

Alternativa, entrando al modulo:

```powershell
cd automation-selenium-pom
..\gradlew clean test aggregate
```

Reporte Serenity:
- `automation-selenium-pom/target/site/serenity/index.html`
- `automation-selenium-pom/build/reports/serenity`

## Nota de Git/GitHub

El repositorio Git quedo centralizado en la raiz `new-reservas-sk`, para que GitHub detecte y publique todos los archivos del proyecto desde un solo punto.
