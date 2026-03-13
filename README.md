# new-reservas-sk

Proyecto de automatizacion UI con **Serenity BDD + Cucumber (Gherkin)** para OpenCart.

Sitio objetivo:
- `http://opencart.abstracta.us/index.php?route=common/home`

## Estructura

- `new-reservas-sk/` (raiz del repositorio Git)
- `automation-selenium/` (modulo principal de pruebas)
- `OLD-HU/`, `REFACTOR-HU/`, `SKAI-INSTRUCTION/` (documentacion y refinamientos)

## Documentacion de la actividad (Semana 4)

- En la carpeta `entregable/` se encuentran los archivos correspondientes a la actividad de la semana 4.
- En la carpeta `OLD-HU/` estan las historias de usuario originales que se manejaron.
- En la carpeta `REFACTOR-HU/` estan las historias de usuario refactorizadas.

Existen 2 tipos de HU en `REFACTOR-HU/`:
- HU creadas por **SKAI**: identificables por el formato `HU-<numero>-refinamiento` (por ejemplo, `HU-06-refinamiento.md`, `HU-07-refinamiento.md`).
- HU creadas por **GPT-5.3**: identificables por el formato `HU-<nombre>-REFACTOR-GPT` (por ejemplo, `HU-06-REFACTOR-GPT.md`, `HU-07-REFACTOR-GPT.md`, `HU-NOTIFICACIONES-REFACTOR-GPT.md`).

## Stack actual

- Java 17
- Gradle 8+
- Serenity BDD 3.9.8
- Serenity Cucumber
- Chrome

## Ejecucion

Desde la raiz del proyecto:

```powershell
.\gradlew :automation-selenium:clean :automation-selenium:test :automation-selenium:aggregate
```

Alternativa, entrando al modulo:

```powershell
cd automation-selenium
..\gradlew clean test aggregate
```

Reporte Serenity:
- `automation-selenium/build/reports/serenity`

## Nota de Git/GitHub

El repositorio Git quedo centralizado en la raiz `new-reservas-sk`, para que GitHub detecte y publique todos los archivos del proyecto desde un solo punto.
