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
Feature: Crear ciudad en el catálogo de ubicaciones

  Scenario: TC-01 Alta exitosa de ciudad
    Given que un administrador autenticado tiene los siguientes datos válidos:
      | nombre | pais |
      | Bogota | CO   |
    When envía un POST a /locations/cities con esos datos
    Then la API responde 201 Created con el objeto ciudad creado incluyendo id, nombre, pais, createdAt y createdBy
    And el evento locations.city.created es emitido con el payload mínimo obligatorio

  Scenario: TC-02 País inexistente en catálogo
    Given que un administrador autenticado intenta crear una ciudad con país "XX" que no está en el catálogo
    When envía un POST a /locations/cities con nombre "Bogota" y pais "XX"
    Then la API responde 422 Unprocessable Entity con un mensaje indicando país no válido

  Scenario: TC-03 Ciudad duplicada en mismo país
    Given que ya existe una ciudad "Bogota" en el país "CO"
    When un administrador autenticado intenta crear otra ciudad con nombre "Bogota" y país "CO"
    Then la API responde 409 Conflict indicando que la ciudad ya está registrada en el mismo país

  Scenario: TC-04 Usuario sin permisos intenta crear ciudad
    Given que un usuario autenticado sin privilegios administrativos
    When intenta crear una ciudad con nombre "Bogota" y país "CO"
    Then la API responde 403 Forbidden

  Scenario: TC-05 Nombre de ciudad demasiado corto
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre "A" y país "CO"
    Then la API responde 422 Unprocessable Entity indicando que el nombre no cumple la longitud mínima

  Scenario: TC-06 Nombre de ciudad demasiado largo
    Given que un administrador autenticado
    When envía un POST a /locations/cities con un nombre de 121 caracteres y país "CO"
    Then la API responde 422 Unprocessable Entity indicando que el nombre excede la longitud máxima permitida

  Scenario: TC-07 Nombre con caracteres inválidos
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre "Bogota123!" y país "CO"
    Then la API responde 422 Unprocessable Entity indicando que el nombre contiene caracteres no permitidos

  Scenario: TC-08 Nombre con apóstrofo y guion permitido
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre "San Juan's-Capital" y país "CO"
    Then la API responde 201 Created y la ciudad es creada correctamente

  Scenario: TC-09 Evento emitido tras creación
    Given que un administrador autenticado crea una ciudad con datos válidos
    When la transacción finaliza exitosamente
    Then se publica el evento locations.city.created con el payload mínimo obligatorio incluyendo id, nombre, pais, createdBy, occurredAt y fuente

  Scenario: TC-10 Fallo del bus de eventos
    Given que el bus de eventos no está disponible durante la creación de ciudad
    When un administrador autenticado envía un POST válido a /locations/cities
    Then la ciudad se crea correctamente y la API responde 201 Created
    And el evento queda en mecanismo de reintento por outbox
    And se registra la trazabilidad técnica del incidente

  Scenario: TC-11 Nombre en límite inferior
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre de exactamente 2 caracteres y país válido
    Then la API responde 201 Created y la ciudad es creada correctamente

  Scenario: TC-12 Nombre en límite superior
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre de exactamente 120 caracteres y país válido
    Then la API responde 201 Created y la ciudad es creada correctamente

  Scenario: TC-13 Unicidad insensible a mayúsculas/minúsculas
    Given que existe la ciudad "Bogota" en el país "CO"
    When un administrador autenticado intenta crear la ciudad "BOGOTA" en el país "CO"
    Then la API responde 409 Conflict indicando duplicidad

  Scenario: TC-14 Unicidad con tildes y sin tildes
    Given que existe la ciudad "Medellín" en el país "CO"
    When un administrador autenticado intenta crear la ciudad "Medellin" en el país "CO"
    Then la API responde 409 Conflict indicando duplicidad

  Scenario: TC-15 País en minúsculas
    Given que un administrador autenticado
    When envía un POST a /locations/cities con nombre "Cali" y país "co"
    Then la API responde 422 Unprocessable Entity indicando que el país no es válido

  Scenario: TC-16 Consumidores reciben evento de creación
    Given que la ciudad es creada y el evento es emitido
    When los módulos de espacios, búsqueda y auditoría escuchan el evento
    Then cada módulo procesa el evento correctamente según su responsabilidad
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la Gema | Ajuste realizado por el equipo | ¿Por qué se ajustó? |
|----|--------------------------|-------------------------------|---------------------|
| TC-03 | Ciudad duplicada en mismo país | Dado que ya existe `Bogota` en `CO` en el catalogo de ciudades, cuando se intenta registrar nuevamente la misma dupla `name+country`, entonces el comportamiento actual esperado es `500 Internal Server Error`. | La duplicidad existe en repositorio de datos (`UNIQUE name,country`), pero la API no mapea el error a estado `409`. |
| TC-04 | Usuario sin permisos intenta crear ciudad | Dado un usuario sin token, cuando intenta crear ciudad, entonces la API responde `401 Unauthorized`; y dado un usuario con token valido, puede ejecutar el alta de ciudad. | El endpoint exige autenticación, pero no existe regla de autorización por rol para devolver `403`. |
| TC-09 | Evento emitido tras creación | Dada un alta de ciudad exitosa, cuando finaliza el caso de uso, entonces se publica `locations.city.created` con `cityId`, `name`, `country`, `occurredAt`. | El evento vigente no incluye `createdBy` ni `source`; el payload actual es más corto. |
| TC-11 | Nombre en límite inferior | Dado un nombre de 2 caracteres y pais informado, cuando se realiza el alta, entonces responde `201 Created`. | No existe rol administrador en esta validación, pero sí se evalúa el límite inferior. |
| TC-12 | Nombre en límite superior | Dado un nombre de 120 caracteres y pais informado, cuando se registra, entonces responde `201 Created`. | No existe rol administrador en esta validación, pero sí se evalúa el límite superior. |
| TC-13 | Unicidad insensible a mayúsculas/minúsculas | Dado que existe `Bogota` en `CO`, cuando se intenta crear `BOGOTA` en `CO`, entonces el resultado actual esperado sigue siendo `500` y no `409`. | Aunque la colación de BD detecta colisión, el servicio no la mapea a conflicto funcional. |
| TC-15 | País en minúsculas |  | El caso de uso no valida catálogo de países ni formato ISO; solo exige que `country` no llegue vacío. |

<a name="hu07"></a>
## HU-07 — Listar ciudades

## 1. Casos de Prueba Generados por SKAI

```gherkin
Feature: Listar ciudades para selección de ubicación

  Scenario: TC-01 Mostrar lista de ciudades activas al usuario autenticado
    Given un usuario autenticado con sesión válida
    And existen ciudades activas registradas en el catálogo
    When el usuario accede al dashboard
    Then el sistema consulta el endpoint GET /locations/cities
    And la respuesta contiene la lista de ciudades activas con sus países en español
    And la interfaz presenta la lista de ciudades activas y países en español

  Scenario: TC-02 Mostrar estado vacío si no existen ciudades activas
    Given un usuario autenticado con sesión válida
    And no existen ciudades activas en el catálogo
    When el usuario accede al dashboard
    Then el sistema consulta el endpoint GET /locations/cities
    And la API responde 200 OK con lista vacía
    And la interfaz muestra estado vacío con mensaje informativo

  Scenario: TC-03 Verificar orden alfabético ascendente de ciudades listadas
    Given un usuario autenticado con sesión válida
    And existen varias ciudades activas registradas en el catálogo
    When el usuario consulta la lista de ciudades
    Then la respuesta contiene las ciudades ordenadas alfabéticamente por nombre en español
    And la interfaz muestra las ciudades en ese mismo orden ascendente

  Scenario: TC-04 Rechazo de consulta a usuario no autenticado
    Given un usuario sin sesión válida
    When intenta consultar la lista de ciudades
    Then la API responde con un código 401 Unauthorized
    And la interfaz muestra mensaje de autenticación requerida

  Scenario: TC-05 Manejo de error cuando el servicio no está disponible
    Given un usuario autenticado con sesión válida
    And el servicio de ubicaciones no está disponible
    When el usuario accede al dashboard o intenta refrescar la lista de ciudades
    Then la API responde con un código 503 Service Unavailable
    And la interfaz muestra mensaje de error y un botón para reintentar la consulta

  Scenario: TC-06 Validar formato de nombre de ciudad y país en español
    Given un usuario autenticado con sesión válida
    And existen ciudades activas registradas en el catálogo
    When el usuario accede al dashboard y consulta la lista de ciudades
    Then en la interfaz se visualiza el nombre de la ciudad y el país en español para cada registro

  Scenario: TC-07 Mostrar estado de carga mientras responde el endpoint
    Given un provider de datos disponible para el usuario autenticado
    When el usuario accede al dashboard o refresca la lista de ciudades
    Then la interfaz muestra un indicador de carga hasta recibir la respuesta del endpoint

  Scenario: TC-08 Refrescar la lista al ejecutar acción manual
    Given un usuario autenticado con sesión válida
    And existen ciudades activas en el catálogo
    When el usuario pulsa el botón de recargar la lista de ciudades
    Then el sistema consulta nuevamente el endpoint GET /locations/cities
    And la interfaz presenta la información actualizada según el catálogo

  Scenario: TC-09 Sincronización de cambios del catálogo en tiempo máximo establecido
    Given un usuario autenticado con sesión válida
    And se realiza un cambio en el catálogo de ciudades (alta, baja, modificación de estado)
    When han pasado máximo 60 segundos desde el cambio
    Then la lista de ciudades consultada por el usuario refleja el cambio realizado

  Scenario: TC-10 Verificar contrato de respuesta exitoso
    Given un usuario autenticado con sesión válida
    And existen ciudades activas en el catálogo
    When el usuario consulta el endpoint GET /locations/cities
    Then la respuesta contiene los campos: id, nombre, país, codigoPais, activa, total
    And los tipos de datos corresponden a lo especificado en el contrato

  Scenario: TC-11 Verificar respuesta y visualización con solo una ciudad activa
    Given un usuario autenticado con sesión válida
    And solo existe una ciudad activa en el catálogo
    When el usuario consulta la lista de ciudades
    Then la API responde correctamente con una lista de un solo elemento
    And la interfaz presenta la ciudad y país en español

  Scenario: TC-12 Verificar performance y visualización cuando hay 5.000 ciudades activas
    Given un usuario autenticado con sesión válida
    And existen 5.000 ciudades activas en el catálogo
    When el usuario consulta la lista de ciudades
    Then la respuesta se obtiene en tiempo objetivo (<300ms)
    And la interfaz muestra correctamente todas las ciudades ordenadas alfabéticamente

  Scenario: TC-13 Manejo de campos nulos o datos inválidos en la respuesta
    Given un usuario autenticado con sesión válida
    And la respuesta de la API incluye un registro con algún campo nulo o valor no esperado
    When el usuario consulta la lista de ciudades
    Then la interfaz ignora los registros inválidos o muestra un mensaje de error funcional

  Scenario: TC-14 Visualización de ciudades para perfil administrador
    Given un usuario autenticado con rol de administrador
    When accede al dashboard y consulta la lista de ciudades
    Then la visualización base es igual a la de un usuario autenticado estándar (sin privilegios extra en esta consulta)

  Scenario: TC-15 Reintentar consulta tras error de servicio
    Given un usuario autenticado con sesión válida
    And la consulta inicial de ciudades falla por servicio no disponible
    When el usuario pulsa el botón de reintentar
    Then el sistema vuelve a consultar el endpoint GET /locations/cities
    And la interfaz muestra el resultado actualizado según respuesta

  Scenario: TC-16 No duplicar ciudades en la lista
    Given un usuario autenticado con sesión válida
    And existen ciudades activas en el catálogo con nombres únicos
    When el usuario consulta la lista de ciudades
    Then cada ciudad aparece una sola vez en la lista presentada

  Scenario: TC-17 Consistencia de la lista para accesos simultáneos
    Given múltiples usuarios autenticados accediendo simultáneamente al dashboard
    And el catálogo de ciudades se actualiza mientras tanto
    When cada usuario consulta la lista de ciudades
    Then todos reciben la lista consistente y actualizada según los cambios en un máximo de 60 segundos
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la Gema | Ajuste realizado por el equipo | ¿Por qué se ajustó? |
|----|--------------------------|-------------------------------|---------------------|
| TC-01 | Mostrar lista de ciudades activas al usuario autenticado | Dado un usuario autenticado, cuando consulta GET /locations/cities, entonces el servicio devuelve el catalogo de ciudades ordenado por nombre, sin filtrar por estado activo. | Actualmente el sistema no maneja campo de activa ni ningún otro filtro. |
| TC-02 | Mostrar estado vacío si no existen ciudades activas | Dado un catálogo de ciudades sin registros, cuando se consulta GET /locations/cities, entonces la API responde 200 OK con data: []. | El sistema devuelve lista vacía por ausencia de ciudades, pero no maneja filtro de activas. |
| TC-06 | Validar formato de nombre de ciudad y país en español | Dado el catálogo de ciudades, cuando se obtiene el listado, entonces cada item incluye name y country tal como están persistidos. | Se retornan las ciudades tal como fueron creadas; no existen traducciones para los registros. |
| TC-08 | Refrescar la lista al ejecutar acción manual | Dado un usuario autenticado, cuando dispara recarga manual, entonces el cliente debe invocar nuevamente GET /locations/cities y renderizar la nueva respuesta. |  |
| TC-09 | Sincronización de cambios del catálogo en tiempo máximo establecido | Dado un cambio en el catálogo de ciudades, cuando se consulta nuevamente el endpoint, entonces se reflejan datos actuales sin SLA temporal definido por API. | No existe mecanismo de espera de 60s implementado en el servicio. |
| TC-10 | Verificar contrato de respuesta exitoso | Dada una consulta exitosa, cuando se valida el contrato actual, entonces la respuesta trae ok, data[], message, errorCode; y cada ciudad incluye id, name, country, createdAt, updatedAt. | El sistema no maneja el contrato propuesto con id, nombre, pais, codigoPais, activa, total. |
| TC-11 | Verificar respuesta y visualización con solo una ciudad activa | Dado un catálogo con una sola ciudad, cuando se consulta el listado, entonces data contiene exactamente un elemento. | El sistema no maneja límite mínimo de registros para devolver datos. |
| TC-14 | Visualización de ciudades para perfil administrador | Dado cualquier usuario autenticado (administrador o estándar), cuando consulta ciudades, entonces obtiene la misma respuesta base del catálogo. | El sistema no maneja roles o privilegios para esta consulta. |
| TC-16 | No duplicar ciudades en la lista | Dado un catálogo sin registros duplicados por name+country, cuando se lista ciudades, entonces cada ciudad aparece una sola vez en data. | La tabla cities tiene restricción única por dupla name+country, lo que evita duplicados exactos. |

<a name="hu-notificaciones"></a>
## HU_NOTIFICACIONES — Recordatorios de fin de reserva y vencimiento

## 1. Casos de Prueba Generados por SKAI

```gherkin
Feature: Notificaciones de recordatorio y vencimiento de reservas

  Scenario: TC-01 Agenda recordatorios al crear una reserva válida
    Given que existe un usuario autenticado con datos de reserva completos (reservationId, userId, spaceId, endAt)
    When el usuario crea una nueva reserva
    Then el sistema agenda los recordatorios para los hitos de 15 minutos antes, 5 minutos antes y 10 minutos después del fin de la reserva

  Scenario: TC-02 No agenda recordatorio con datos incompletos
    Given que el backend recibe un evento de reserva creada sin el campo reservationId
    When el sistema procesa el evento
    Then el sistema no agenda ningún recordatorio para esa reserva

  Scenario: TC-03 Envía alerta de 15 minutos antes del fin
    Given que existe una reserva activa agendada y el tiempo actual es igual o mayor a endAt menos 15 minutos
    When el proceso de recordatorio ejecuta el ciclo de revisión
    Then el sistema publica una alerta tipo 15 minutos para esa reserva una sola vez

  Scenario: TC-04 Envía alerta de 5 minutos antes del fin
    Given que existe una reserva activa agendada y el tiempo actual es igual o mayor a endAt menos 5 minutos
    When el proceso de recordatorio ejecuta el ciclo de revisión
    Then el sistema publica una alerta tipo 5 minutos para esa reserva una sola vez

  Scenario: TC-05 Envía alerta de 10 minutos después del fin
    Given que existe una reserva activa agendada y el tiempo actual es igual o mayor a endAt más 10 minutos y la reserva no ha sido devuelta ni cancelada
    When el proceso de recordatorio ejecuta el ciclo de revisión
    Then el sistema publica una alerta tipo overdue para esa reserva una sola vez

  Scenario: TC-06 No envía alertas duplicadas por hito
    Given que el sistema ya publicó una alerta tipo 15 minutos para una reserva específica
    When el proceso de recordatorio ejecuta nuevos ciclos antes del fin de la reserva
    Then el sistema no publica nuevamente la alerta tipo 15 minutos para esa reserva

  Scenario: TC-07 Detiene recordatorios tras cancelar reserva
    Given que existe una reserva con recordatorios pendientes
    When el usuario cancela la reserva antes de que se cumplan los hitos
    Then el sistema elimina todos los recordatorios futuros y no publica nuevas alertas para esa reserva

  Scenario: TC-08 Detiene recordatorios tras devolver reserva
    Given que existe una reserva con recordatorios pendientes
    When el usuario devuelve la reserva antes de que se cumplan los hitos
    Then el sistema elimina todos los recordatorios futuros y no publica nuevas alertas para esa reserva

  Scenario: TC-09 Muestra alerta de 15 minutos en Mis Reservas
    Given que el usuario autenticado está suscrito al canal de notificaciones correspondiente
    When el sistema publica una alerta tipo 15 minutos para una reserva activa
    Then la interfaz muestra una alerta de tipo 15 minutos en el componente ReminderAlertBanner sin duplicados

  Scenario: TC-10 Permite descartar una alerta individual
    Given que el usuario ve una alerta activa en el componente ReminderAlertBanner
    When el usuario selecciona la opción de descartar esa alerta
    Then la alerta desaparece de la vista de Mis Reservas

  Scenario: TC-11 Permite limpiar todas las alertas visibles
    Given que el usuario tiene múltiples alertas visibles en Mis Reservas
    When el usuario selecciona la opción de limpiar todas las alertas
    Then todas las alertas desaparecen del componente ReminderAlertBanner

  Scenario: TC-12 Maneja payload inválido sin interrumpir flujo
    Given que el sistema recibe un mensaje de evento de recordatorio con campos faltantes o formato incorrecto
    When procesa el mensaje en el backend o frontend
    Then descarta el mensaje inválido y continúa procesando el resto de eventos normalmente

  Scenario: TC-13 No duplica alertas por reserva y tipo
    Given que el usuario recibe una alerta tipo 5 minutos para una misma reserva
    When el sistema recibe un evento repetido de alerta tipo 5 minutos para esa reserva
    Then no se muestra más de una alerta de ese tipo para la misma reserva en pantalla

  Scenario: TC-14 Reconexión automática de suscripción a notificaciones
    Given que el usuario está en la página Mis Reservas y la conexión WebSocket se pierde
    When la conexión se restablece antes de 5 segundos
    Then el usuario se resuscribe automáticamente a los canales de notificaciones y recibe las alertas pendientes

  Scenario: TC-15 Limpieza de recordatorios vencidos después de 1 día
    Given que existe una reserva cuyo endAt fue hace más de un día
    When el proceso de limpieza ejecuta su ciclo
    Then el sistema elimina el estado temporal relacionado a esa reserva

  Scenario: TC-16 No agenda recordatorios para reservas canceladas o devueltas antes del fin
    Given que el sistema recibe un evento de cancelación o devolución para una reserva antes de su endAt
    When procesa el evento
    Then no agenda ni publica ningún recordatorio futuro para esa reserva

  Scenario: TC-17 Publica alerta dentro de la ventana del scheduler
    Given que el tiempo actual alcanza el hito de 15 minutos antes del fin de la reserva
    When el proceso de revisión se ejecuta en un ciclo de 30 segundos
    Then el sistema publica la alerta dentro de la ventana de 30 segundos posterior al hito

  Scenario: TC-18 Secuencia completa de alertas por reserva
    Given que existe una reserva activa y el usuario no devuelve ni cancela la reserva
    When se cumplen los hitos de 15 minutos antes, 5 minutos antes y 10 minutos después del fin
    Then el usuario recibe en orden una alerta de 15 minutos, luego de 5 minutos y luego de overdue, sin duplicados y en el componente ReminderAlertBanner

  Scenario: TC-19 No muestra alertas después de cancelar o devolver
    Given que el usuario tiene una reserva activa y recibe una alerta de 15 minutos
    When el usuario cancela o devuelve la reserva antes de los siguientes hitos
    Then el sistema no publica ni muestra las alertas de 5 minutos ni overdue para esa reserva

  Scenario: TC-20 Usa mensaje por defecto según tipo si falta mensaje en payload
    Given que el sistema publica una alerta sin texto de mensaje específico en el payload
    When el frontend recibe la alerta
    Then muestra un mensaje por defecto correspondiente al tipo de alerta en ReminderAlertBanner
```

## 2. Tabla de Ajustes Realizados por el Equipo

| ID | Caso generado por la Gema | Ajuste realizado por el equipo | ¿Por qué se ajustó? |
|----|--------------------------|-------------------------------|---------------------|
| TC-01 | Agenda recordatorios al crear una reserva válida | Dado un evento bookings.reservation.created valido, cuando el servicio lo procesa, entonces registra estado temporal de la reserva para evaluar hitos 15m, 5m y overdue 10m en cada ciclo del scheduler. | El sistema no crea Jobs programados por reserva, sino que mantiene un estado temporal en memoria para evaluar los hitos en publishReminders(). |
| TC-02 | No agenda recordatorio con datos incompletos | Dado un evento de creacion con reservationId faltante o invalido, cuando se procesa, entonces no se registra estado de recordatorios para esa reserva. | handleEvent() descarta payloads incompletos y no agrega entrada al mapa de recordatorios. |
| TC-09 | Muestra alerta de 15 minutos en Mis Reservas | Dado que se cumple hito 15m, cuando se publica el evento de recordatorio, entonces notifications-service lo emite por /topic/events y temas derivados. | El microservicio emite eventos WebSocket; la deduplicación/render de banner es responsabilidad del frontend. |
| TC-15 | Limpieza de recordatorios vencidos después de 1 día | Dado un estado de reserva con endAt vencido por mas de 1 dia, cuando corre el scheduler, entonces se elimina el estado temporal de seguimiento. | publishReminders() remueve entradas después de 1 día de finalizada la reserva. |

<a name="resumen-global"></a>
## Resumen Global del Proyecto

| Historia | Casos generados | Casos con ajuste/justificación |
|---|---:|---:|
| HU-06 | 16 | 7 |
| HU-07 | 17 | 9 |
| HU_NOTIFICACIONES | 20 | 4 |
| **Total** | **53** | **20** |
