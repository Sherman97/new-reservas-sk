# Informe comparativo de refinamiento de historias de usuario

Fuente usada para este comparativo:
- **HU original**: carpeta `OLD-HU/`
- **HU refinada**: carpeta `REFACTOR-HU/` (versiones GPT)

## HU-06: Crear ciudad

| HU original (OLD-HU) | HU refinada (REFACTOR-HU, GPT) | Diferencias encontradas (lenguaje simple) |
|---|---|---|
| "Como administrador, quiero registrar una nueva ciudad..." | "Como Administrador de Catálogo, quiero registrar una Ciudad..." | La refinada nombra mejor el rol del dominio: **Administrador de Catálogo**. |
| Criterios básicos: enviar nombre/país, unicidad, evento y devolver ID | Reglas completas de negocio + criterios Given/When/Then + errores esperados | La refinada pasa de idea general a comportamiento operativo claro para Backend, Frontend y QA. |
| No define permisos con detalle | Incluye `403 Forbidden` para usuario sin permisos | Se aclara quién puede dar de alta una Ciudad en el Catálogo de Ubicaciones. |
| No define validaciones de obligatoriedad de forma explícita | Incluye validación de `nombre` y `pais` obligatorios con `422` | Se reduce ambigüedad en datos de entrada. |
| Evento `locations.city.created` solo nombrado | Evento con payload sugerido (`cityId`, `name`, `country`, `occurredAt`) | La refinada define qué datos viajan en el evento de dominio. |
| Datos técnicos mínimos (endpoint/servicio/tabla) | Contrato API base de request/response y errores (`403`, `409`, `422`, `500`) | Se deja trazado el contrato funcional para implementar y probar la HU. |

## HU-07: Listar ciudades

| HU original (OLD-HU) | HU refinada (REFACTOR-HU, GPT) | Diferencias encontradas (lenguaje simple) |
|---|---|---|
| "Como usuario autenticado..." | "Como Colaborador autenticado..." | La refinada usa el rol del dominio: **Colaborador**. |
| "Se muestra una lista..." y "se carga al Dashboard" | Define alcance, reglas y criterios de aceptación completos | La refinada cierra cómo debe comportarse la consulta de Ciudades. |
| No define orden de lista | Orden alfabético por nombre de Ciudad | Se estandariza la experiencia en Dashboard y se facilita la prueba. |
| No define comportamiento cuando no hay datos | Estado vacío con mensaje "No hay ciudades disponibles" | Se diferencia claramente entre "sin datos" y "error". |
| No define manejo de fallos | Manejo de error de Servicio de Ubicaciones + reintento | La HU ya contempla contingencias del flujo real. |
| Datos técnicos mínimos | Contrato API con respuesta ejemplo y errores `401/403/500` | Se deja más claro el acuerdo entre Frontend y Servicio de Ubicaciones. |
| No incluye evaluación de calidad de la HU | Agrega Claridad/Ambigüedades, INVEST, Coherencia y Preguntas | La refinada queda lista para refinamiento funcional y técnico. |

## HU_NOTIFICACIONES: Recordatorios de fin de reserva y vencimiento

| HU original (OLD-HU) | HU refinada (REFACTOR-HU, GPT) | Diferencias encontradas (lenguaje simple) |
|---|---|---|
| Documento técnico amplio con contexto, trazabilidad de código y pruebas existentes | HU estructurada por título, descripción breve, historia, reglas y criterios | La refinada prioriza lectura funcional rápida para implementación. |
| Objetivo funcional detallado pero extenso | Objetivo directo: alertar `15m`, `5m` y `overdue10m` en Mis Reservas | Se mantiene el mismo negocio, pero con forma más clara para el equipo. |
| Reglas y flujo técnico en detalle de implementación | Reglas de negocio concretas (no duplicación, cancelación/devolución, reconexión) | La refinada conserva la lógica clave con lenguaje más operativo. |
| Mensajes de alerta no estaban centralizados como catálogo funcional | Incluye ejemplos de mensajes por tipo y fallback de error | Se mejora consistencia de experiencia para el Colaborador. |
| Menciona UTC en lógica técnica | Explicita UTC en Backend y visualización local en Frontend | Se evita confusión de hora para el usuario final. |
| Incluye NFR y trazabilidad de clases/archivos del código | Incluye INVEST, coherencia y preguntas de refinamiento | La refinada orienta decisiones de producto/arquitectura para siguientes iteraciones. |

