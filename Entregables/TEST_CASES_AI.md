# TEST_CASES_AI.md
# Matriz de Casos de Prueba — Reservas-SK
### Historias analizadas: HU-06 | HU-07 | HU_NOTIFICACIONES

## 📋 Índice
1. [HU-06 — Crear ciudad](#hu06)
2. [HU-07 — Listar ciudades](#hu07)
3. [HU_NOTIFICACIONES — Recordatorios de fin de reserva y vencimiento](#hu-notificaciones)
4. [Resumen Global del Proyecto](#resumen-global)

<a name="hu06"></a>
## HU-06 — Crear ciudad

## 1. Casos de Prueba Generados por SKAI

```gherkin
Feature: Crear ciudad en el Catálogo de Ubicaciones

  Scenario: Caso de prueba 1 - Alta exitosa de una ciudad
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    And existe una combinación de nombre y país que no está registrada
    When envío una solicitud POST a /locations/cities con nombre "Bogota" y país "Colombia"
    Then el sistema responde con código 201 Created
    And la respuesta contiene el objeto ciudad con un identificador único
    And el evento de dominio locations.city.created es emitido con los datos correctos
    And la trazabilidad del usuario que realiza el alta queda registrada

  Scenario: Caso de prueba 2 - Validación de obligatoriedad del campo país
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con el campo país vacío o ausente
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje que indica que el país es obligatorio

  Scenario: Caso de prueba 3 - Validación de obligatoriedad del campo nombre
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con el campo nombre vacío o ausente
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje que indica que el nombre es obligatorio

  Scenario: Caso de prueba 4 - Validación de unicidad de ciudad por país
    Given que ya existe una ciudad registrada con nombre "Bogota" y país "Colombia"
    And soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When intento registrar una ciudad con nombre "Bogota" y país "Colombia"
    Then el sistema responde con código 409 Conflict
    And muestra un mensaje indicando que la ciudad ya está registrada

  Scenario: Caso de prueba 5 - Usuario sin permisos administrativos
    Given que soy un usuario autenticado sin permisos de administración del Catálogo de Ubicaciones
    When intento crear una ciudad enviando una solicitud POST a /locations/cities
    Then el sistema responde con código 403 Forbidden

  Scenario: Caso de prueba 6 - Error técnico durante el alta de ciudad
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    And envío una solicitud válida para crear una ciudad
    And ocurre un error inesperado en la persistencia o integración
    When intento registrar la ciudad
    Then el sistema responde con un código de error controlado (500 o similar)
    And muestra un mensaje de fallo en el alta de ciudad sin exponer información sensible

  Scenario: Caso de prueba 7 - Validación de longitud máxima de nombre de ciudad
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con un nombre que excede la longitud máxima permitida
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje indicando que el nombre excede la longitud permitida

  Scenario: Caso de prueba 8 - Validación de longitud máxima de país
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con un país que excede la longitud máxima permitida
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje indicando que el país excede la longitud permitida

  Scenario: Caso de prueba 9 - Validación de caracteres inválidos en nombre
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con un nombre que contiene caracteres no permitidos
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje indicando que el nombre contiene caracteres inválidos

  Scenario: Caso de prueba 10 - Validación de caracteres inválidos en país
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con un país que contiene caracteres no permitidos
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje indicando que el país contiene caracteres inválidos

  Scenario: Caso de prueba 11 - Validación con nombre y país con espacios iniciales/finales
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con nombre o país que tienen espacios al inicio o final
    Then el sistema responde con código 201 Created
    And almacena los valores normalizados sin espacios no deseados

  Scenario: Caso de prueba 12 - Validación de sesión expirada o token inválido
    Given que mi sesión ha expirado o el token JWT es inválido
    When intento crear una ciudad enviando una solicitud POST a /locations/cities
    Then el sistema responde con código 401 Unauthorized
    And solicita volver a iniciar sesión

  Scenario: Caso de prueba 13 - Validación de país inexistente en catálogo
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    When envío una solicitud POST a /locations/cities con un país que no existe en el catálogo permitido (por ejemplo, no es un país ISO)
    Then el sistema responde con código 422 Unprocessable Entity
    And muestra un mensaje indicando que el país no es válido

  Scenario: Caso de prueba 14 - Prueba de concurrencia en creación de ciudad
    Given que soy un usuario autenticado con permisos de administración del Catálogo de Ubicaciones
    And otro usuario también está intentando crear la misma ciudad al mismo tiempo
    When ambos envían la solicitud POST simultáneamente con nombre y país idénticos
    Then solo una solicitud es exitosa con código 201 Created
    And la otra recibe código 409 Conflict
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la SKAI | Ajuste realizado por el equipo | ¿Por qué se ajustó? |
|----|--------------------------|-------------------------------|---------------------|
| Caso 4 | Validación de unicidad de ciudad por país | Dado que ya existe `Bogota` en `CO` en el catálogo de ciudades, cuando se intenta registrar nuevamente la misma dupla `name+country`, entonces el comportamiento actual esperado es `500 Internal Server Error`. | La duplicidad existe en repositorio de datos (`UNIQUE name,country`), pero la API no mapea el error a estado `409`. |
| Caso 5 | Usuario sin permisos administrativos | Dado un usuario sin token, cuando intenta crear ciudad, entonces la API responde `401 Unauthorized`; y dado un usuario con token válido, puede ejecutar el alta de ciudad. | El endpoint exige autenticación, pero no existe regla de autorización por rol para devolver `403`. |
| Caso 2 y 3 | Validación de obligatoriedad del campo país / nombre | Dado un payload sin `name` o sin `country`, cuando se invoca `POST /locations/cities`, entonces el resultado esperado en la implementación vigente es `400 Bad Request` (no `422`). | En la ruta actual, la validación de DTO cae por el circuito de `MethodArgumentNotValidException`, por lo que el estado observado se resuelve en 400. |
| Caso 13 | Validación de país inexistente en catálogo | Dado un `country` no-ISO pero no vacío, cuando se crea ciudad, entonces el sistema puede aceptar el valor y persistirlo si supera validación básica. | El servicio no exhibe una verificación contra catálogo ISO ni una lista controlada de países en esta versión. |

<a name="hu07"></a>
## HU-07 — Listar ciudades

## 1. Casos de Prueba Generados por SKAI

```gherkin
Feature: Listar ciudades para selección de ubicación

  Scenario: Caso de prueba 1 - Carga y visualización correcta de la lista de Ciudades
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema realiza una consulta GET /locations/cities y existen ciudades registradas
    Then el sistema muestra la lista de ciudades disponibles con los datos id, nombre y país
    And la lista aparece ordenada alfabéticamente por nombre de ciudad.

  Scenario: Caso de prueba 2 - Estado vacío al no existir ciudades
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema realiza una consulta GET /locations/cities y no existen ciudades registradas
    Then el sistema muestra el mensaje "No hay ciudades disponibles"
    And no muestra error técnico.

  Scenario: Caso de prueba 3 - Manejo de error en el servicio de ubicaciones
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema realiza una consulta GET /locations/cities y ocurre un error de backend o integración
    Then el sistema muestra un mensaje de error controlado al usuario
    And permite reintentar la carga.

  Scenario: Caso de prueba 4 - Acceso rechazado para usuario no autenticado
    Given que un usuario sin sesión válida intenta acceder al Dashboard y consultar ciudades
    When el sistema recibe la solicitud GET /locations/cities
    Then el sistema rechaza la operación según política de seguridad
    And redirige al flujo de autenticación.

  Scenario: Caso de prueba 5 - Verificación de datos mínimos en la lista de ciudades
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema muestra la lista de ciudades disponibles
    Then cada ciudad de la lista contiene los datos id, nombre y país.

  Scenario: Caso de prueba 6 - Consistencia de la lista de ciudades con el catálogo activo
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema consulta el catálogo de ciudades actualizado
    Then la lista de ciudades mostrada es consistente con los datos activos en la base de datos.

  Scenario: Caso de prueba 7 - Reintento de carga tras error de servicio
    Given que un Colaborador autenticado accede al Dashboard y ocurre un error de backend al consultar ciudades
    When el usuario selecciona la opción de reintentar
    Then el sistema realiza nuevamente la consulta GET /locations/cities
    And muestra el resultado actual según la respuesta recibida.

  Scenario: Caso de prueba 8 - Acceso con token inválido o expirado
    Given que un usuario tiene un token ausente, inválido o expirado
    When intenta acceder al Dashboard y consultar ciudades
    Then el sistema responde con error 401 Unauthorized
    And redirige al usuario al flujo de autenticación.

  Scenario: Caso de prueba 9 - Usuario autenticado sin permisos
    Given que un usuario autenticado no tiene permisos para consultar ciudades (según política definida)
    When el sistema recibe la solicitud GET /locations/cities
    Then el sistema responde con error 403 Forbidden
    And muestra mensaje de acceso denegado según política de seguridad.

  Scenario: Caso de prueba 10 - Límite de longitud para nombre y país de ciudad
    Given que un Colaborador autenticado accede al Dashboard
    When la lista de ciudades incluye una ciudad con nombre o país igual a la longitud máxima permitida
    Then el sistema muestra correctamente el nombre y país completos en la interfaz de usuario.

  Scenario: Caso de prueba 11 - Exceso en la longitud de nombre o país de ciudad
    Given que un Colaborador autenticado accede al Dashboard
    When el sistema recibe una ciudad con nombre o país que excede la longitud máxima permitida
    Then el sistema muestra un mensaje de error controlado
    And no presenta datos corruptos o incompletos en la lista.

  Scenario: Caso de prueba 12 - Actualización en tiempo real de la lista de ciudades
    Given que un Colaborador autenticado visualiza la lista de ciudades en el Dashboard
    When se agrega, modifica o elimina una ciudad en el catálogo de ubicaciones
    Then el sistema actualiza automáticamente la lista de ciudades sin requerir recarga manual (si está implementado).

  Scenario: Caso de prueba 13 - Combinación de ciudades con datos similares
    Given que un Colaborador autenticado accede al Dashboard
    When existen ciudades con nombres similares pero países distintos
    Then el sistema muestra cada ciudad de forma diferenciada y correcta en la lista ordenada.

  Scenario: Caso de prueba 14 - Inconsistencia en respuesta del backend
    Given que un Colaborador autenticado accede al Dashboard
    When el backend responde con datos incompletos o campos faltantes en la lista de ciudades
    Then el sistema muestra un mensaje de error controlado
    And no permite seleccionar una ciudad con datos faltantes.

  Scenario: Caso de prueba 15 - Estado vacío seguido de alta de ciudad
    Given que un Colaborador autenticado ve el mensaje "No hay ciudades disponibles"
    When se ingresa una nueva ciudad en el catálogo
    Then el sistema permite visualizar la nueva ciudad al reintentar la carga.
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la SKAI | Ajuste realizado por el equipo | ¿Por qué se ajustó?                                                                                                                                                                                              |
|----|--------------------------|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Caso 1 | Carga y visualización correcta de la lista de Ciudades | Dado un usuario autenticado, cuando consulta GET /locations/cities, entonces el servicio devuelve el catálogo de ciudades ordenado por nombre, sin filtrar por estado activo. | Actualmente el sistema no maneja campo de activa ni ningún otro filtro.                                                                                                                                          |
| Caso 2 | Estado vacío al no existir ciudades | Dado un catálogo de ciudades sin registros, cuando se consulta GET /locations/cities, entonces la API responde 200 OK con data: []. | El sistema devuelve lista vacía por ausencia de ciudades, pero no maneja ningun tipo de mensaje que indique que no hay datos y registros.                                                                        |
| Caso 5 | Verificación de datos mínimos en la lista de ciudades | Dada una consulta exitosa, cuando se valida el contrato actual, entonces la respuesta trae ok, data[], message, errorCode; y cada ciudad incluye id, name, country, createdAt, updatedAt. | El sistema no maneja el contrato propuesto con id, nombre y país.                                                                                                                                                |
| Caso 9 | Usuario autenticado sin permisos | Dado cualquier usuario autenticado (administrador o estándar), cuando consulta ciudades, entonces obtiene la misma respuesta base del catálogo. | El sistema no maneja roles o privilegios para esta consulta, solo se exige que tenga un token valido                                                                                                             |
| Caso 12 | Actualización en tiempo real de la lista de ciudades | Dado un cambio en el catálogo de ciudades, cuando se consulta nuevamente el endpoint, entonces se reflejan datos actuales sin SLA temporal definido por API. | No existe mecanismo de espera de 60s implementado en el servicio, tambien el no permite realizar modificaciones o eliminar una ciudad o pais, ya que actualment estos registros se encuentran creado por seeders |
| Caso 14 | Inconsistencia en respuesta del backend | Dada la respuesta de `GET /locations/cities`, cuando el contrato es válido, entonces cada elemento de `data` incluye siempre `id`, `name`, `country`, `createdAt`, `updatedAt`; por tanto no aplica el flujo de “campos faltantes” como comportamiento nominal del servicio. | El contrato expuesto por el controlador y mapper no sugiere respuestas parciales como caso funcional esperado, sino escenario de falla fuera del flujo principal.                                                |

<a name="hu-notificaciones"></a>
## HU_NOTIFICACIONES — Recordatorios de fin de reserva y vencimiento

## 1. Casos de Prueba Generados por SKAI

```gherkin
Feature: Notificaciones de recordatorio y vencimiento de reservas

  Scenario: Caso de prueba 1 - Notificación 15 minutos antes
    Given que existe una reserva activa con un campo endAt válido para el usuario autenticado
    When faltan exactamente 15 minutos para la finalización de la reserva
    Then se emite una alerta de tipo 15m para la reserva
    And el usuario visualiza la alerta en tiempo real en la sección "Mis Reservas"
    And el mensaje mostrado es "Tu reserva termina en 15 minutos. Prepárate para la devolución del espacio."

  Scenario: Caso de prueba 2 - Notificación 5 minutos antes
    Given que existe una reserva activa con un campo endAt válido para el usuario autenticado
    When faltan exactamente 5 minutos para la finalización de la reserva
    Then se emite una alerta de tipo 5m para la reserva
    And el usuario visualiza la alerta en tiempo real en la sección "Mis Reservas"
    And el mensaje mostrado es "Tu reserva termina en 5 minutos. Realiza la devolución a tiempo."

  Scenario: Caso de prueba 3 - Notificación overdue 10 minutos
    Given que existe una reserva activa con un campo endAt válido y no se ha registrado devolución
    When han transcurrido 10 minutos después de la hora de endAt de la reserva
    Then se emite una alerta de tipo overdue10m para la reserva
    And el usuario visualiza la alerta en tiempo real en la sección "Mis Reservas"
    And el mensaje mostrado es "Han pasado 10 minutos desde el fin de tu reserva y aún no registra devolución."

  Scenario: Caso de prueba 4 - Alerta no duplicada
    Given que una alerta de tipo 15m ya fue emitida para una reserva específica
    When el proceso de notificación se ejecuta nuevamente para la misma reserva y tipo
    Then no se vuelve a emitir la misma alerta de tipo 15m para esa reserva

  Scenario: Caso de prueba 5 - Cancelación detiene alertas
    Given que existe una reserva activa con alertas programadas
    When se recibe un evento de cancelación de la reserva antes de la finalización o vencimiento
    Then se eliminan los recordatorios pendientes para esa reserva
    And no se generan nuevas alertas para esa reserva tras la cancelación

  Scenario: Caso de prueba 6 - Devolución detiene alertas
    Given que existe una reserva activa con alertas programadas
    When se recibe un evento de devolución de la reserva antes de la generación de alertas pendientes
    Then se eliminan los recordatorios pendientes para esa reserva
    And no se generan nuevas alertas para esa reserva tras la devolución

  Scenario: Caso de prueba 7 - Reconexión WebSocket y recuperación de alertas
    Given que el usuario autenticado experimenta una desconexión temporal del WebSocket durante el periodo de alertas
    When el usuario se reconecta al WebSocket
    Then el sistema recupera y entrega al usuario las alertas vigentes no recibidas según la política definida
    And el usuario puede visualizar las alertas en la sección "Mis Reservas"

  Scenario: Caso de prueba 8 - Descartar y limpiar alertas
    Given que el usuario autenticado tiene una o más alertas visibles en la sección "Mis Reservas"
    When el usuario descarta una alerta específica
    Then la alerta descartada ya no se muestra en el listado visible
    And el usuario puede limpiar el listado de alertas si así lo desea

  Scenario: Caso de prueba 9 - Visualización de mensajes correctos
    Given que el usuario autenticado recibe alertas tipo 15m, 5m o overdue10m
    When la alerta es visualizada en el Frontend
    Then el mensaje mostrado corresponde exactamente al tipo de alerta recibido
    And si no existe mensaje específico, se muestra un mensaje de fallback por defecto

  Scenario: Caso de prueba 10 - Verificación de datos válidos para alertas
    Given que existe una reserva inactiva o cuyo reservationId, userId, spaceId o endAt es inválido o nulo
    When llega el momento de emitir una alerta de proximidad o vencimiento
    Then no se emite ninguna alerta relacionada con dicha reserva

  Scenario: Caso de prueba 11 - Visualización de hora local
    Given que el backend calcula los eventos de alerta en UTC
    When el usuario autenticado visualiza la alerta en el Frontend
    Then la hora mostrada al usuario está correctamente convertida a la zona horaria local configurada

  Scenario: Caso de prueba 12 - Reserva con endAt en el límite
    Given que una reserva activa tiene un campo endAt exactamente en el límite de los 15 minutos o 5 minutos para la finalización
    When se ejecuta el proceso de notificación
    Then se emite la alerta correspondiente de tipo 15m o 5m, según corresponda

  Scenario: Caso de prueba 13 - Fallback por error de carga
    Given que ocurre un error al intentar cargar las notificaciones en el Frontend
    When el usuario accede a la sección "Mis Reservas"
    Then se muestra el mensaje "No fue posible cargar notificaciones en este momento. Intenta nuevamente."

  Scenario: Caso de prueba 14 - Alertas en múltiples sesiones
    Given que el usuario autenticado inicia sesión en diferentes dispositivos o navegadores simultáneamente
    When se genera una alerta de proximidad o vencimiento para una reserva activa
    Then todas las sesiones activas reciben la alerta en tiempo real y de forma sincronizada

  Scenario: Caso de prueba 15 - No recepción de alertas por usuario no autenticado
    Given que un usuario no ha iniciado sesión o su token es inválido o expiró
    When se genera una alerta de cualquier tipo para una reserva
    Then el usuario no recibe ninguna alerta mientras no esté autenticado nuevamente

  Scenario: Caso de prueba 16 - Unicidad de alerta por reserva y tipo
    Given que existen múltiples reservas activas para un usuario
    When se generan alertas de proximidad o vencimiento
    Then por cada reserva solo se emite una alerta de cada tipo (15m, 5m, overdue10m)
    And no se emiten alertas duplicadas para la misma reserva y el mismo tipo

  Scenario: Caso de prueba 17 - Alertas en reservas con equipos asociados
    Given que una reserva activa tiene equipos asociados válidos y disponibles
    When se cumplen las condiciones de tiempo para emitir una alerta
    Then la alerta se emite correctamente y se visualiza en tiempo real

  Scenario: Caso de prueba 18 - Sin alertas para reservas canceladas o devueltas
    Given que una reserva está en estado cancelado o devuelto antes de que se cumplan los tiempos de alerta
    When se alcanza el momento de emitir una alerta
    Then no se emite ninguna alerta para esa reserva
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la SKAI | Ajuste realizado por el equipo | ¿Por qué se ajustó?                                                                                                                                   |
|----|--------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| Caso 10 | Verificación de datos válidos para alertas | Dado un evento de creación con `reservationId` faltante o inválido, cuando se procesa, entonces no se registra estado de recordatorios para esa reserva. | `handleEvent()` descarta payloads incompletos y no agrega entrada al mapa de recordatorios.                                                           |
| Caso 7 | Reconexión WebSocket y recuperación de alertas | Dada una desconexión del cliente, cuando vuelve a conectar, entonces solo queda garantizada la recepción de eventos nuevos publicados desde ese momento. | En la implementación actual no se insinúa un mecanismo de backlog/replay de alertas pendientes tras reconexión.                                       |
| Caso 8 | Descartar y limpiar alertas | Dado que el usuario descarta o limpia alertas, cuando se valida el backend de notificaciones, entonces no existe operación server-side asociada; la acción pertenece a estado UI. | El microservicio expone emisión por evento, pero no una API de persistencia/gestión de alertas leídas o descartadas.                                  |
| Caso 15 | No recepción de alertas por usuario no autenticado | Dado un cliente conectado al broker STOMP, cuando se emiten recordatorios, entonces el servicio los publica en topics globales sin filtro por usuario autenticado en esta capa. | El sistema actualmente limita el acceso global por medio de un token valido, de lo contrario no podría tener ningun tipo de inteaccion con el sistema |

<a name="resumen-global"></a>
## Resumen Global del Proyecto

| Historia | Casos generados | Casos con ajuste/justificación |
|---|---:|---:|
| HU-06 | 14 | 5 |
| HU-07 | 15 | 6 |
| HU_NOTIFICACIONES | 18 | 7 |
| **Total** | **47** | **18** |
