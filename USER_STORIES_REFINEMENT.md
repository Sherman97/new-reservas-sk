# Informe comparativo de refinamiento de historias de usuario

## HU-06: Crear ciudad

| HU original | HU refinada por la instruccion | Diferencias detectadas |
|---|---|---|
| "Como administrador, quiero registrar una nueva ciudad con su nombre y pais..." | "Cuando la malla territorial todavia tiene un hueco... trazar una ciudad nueva..." | La intencion es la misma, pero la refinada cambia de una frase recta a una narracion mas situada. Antes era un enunciado breve; ahora pone escena y proposito con mas contexto operativo. |
| "Se envia nombre y pais de la ciudad." | "pais debe validarse contra un catalogo vigente..." y "nombre debe cumplir validaciones de formato..." | Antes bastaba tocar la puerta con dos campos. Ahora la puerta tiene filtro: pais valido, nombre con longitud (2-120) y caracteres permitidos. |
| "La combinacion nombre-pais debe ser unica." | "Ciudad duplicada... Then la API responde 409 Conflict" | La regla de unicidad ya estaba, pero ahora se dice como duele cuando se rompe: codigo de error especifico y escenario concreto de rechazo. |
| "Al crearse exitosamente, se emite el evento locations.city.created." | "Evento emitido... payload minimo obligatorio" + bloque JSON con `eventId`, `occurredAt`, `city`, `meta` | En la vieja se nombraba la campana; en la refinada se describe su sonido. Ahora el evento trae cuerpo definido y datos rastreables para consumidores. |
| "Se devuelve la ciudad creada con su ID." | Response 201 con `id`, `nombre`, `pais`, `createdAt`, `createdBy` | Se paso de promesa minima (solo ID) a contrato de salida completo, con marca de tiempo y autoria. |
| (No aparece manejo de autorizacion) | "Solo roles con permiso administrativo..." + escenario `403 Forbidden` | Antes no se veia el guardia en la puerta. Ahora se explicita quien entra y que pasa cuando el rol no corresponde. |
| (No aparece manejo de caida del bus) | "Si el bus de eventos no esta disponible... no se revierte... outbox... trazabilidad" | Se agrego resiliencia. La vieja asumia cielo despejado; la refinada contempla tormenta y define como seguir sin perder la transaccion principal. |
| (Sin estructura de pruebas detallada) | Criterios Given/When/Then para alta, pais invalido, duplicado, no autorizado y evento | Se volvio mas testeable: ahora hay huellas claras para QA, no solo intenciones generales. |

## HU-07: Listar ciudades

| HU original | HU refinada por la instruccion | Diferencias detectadas |
|---|---|---|
| "Como usuario autenticado, quiero ver la lista de ciudades disponibles..." | "quiero consultar el catalogo de ciudades disponibles, con su pais asociado..." | La refinada conserva el objetivo, pero lo amarra mejor al flujo de reserva y al contexto de catalogo. |
| "Se muestra una lista de todas las ciudades registradas..." | "Por defecto se muestran solo ciudades activas." | Cambio clave de alcance: antes "todas", ahora "activas por defecto". Se limpia la ambiguedad sobre inactivas/eliminadas logicas. |
| (Sin regla de orden) | "La lista se entrega ordenada alfabeticamente por nombre" | Antes la lista podia llegar como cayera. Ahora tiene norte fijo de ordenamiento para UX y pruebas. |
| (Sin idioma definido) | "El texto de ciudad y pais se presenta en espanol." | Se agrega definicion de idioma; se evita que cada pantalla hable en dialecto distinto. |
| "La lista se carga al acceder al dashboard." | "carga inicial automatica" + "puede refrescarse manualmente" + "cambios... maximo 60 segundos" | Se conserva la carga inicial, pero se agrega respiracion continua: refresco y expectativa temporal de actualizacion. |
| (Sin comportamiento de error) | "Error de servicio... mostrar mensaje y opcion de reintento" + `503` | Antes no habia paraguas para falla del endpoint. Ahora se define que ve el usuario cuando la consulta cae. |
| (Sin comportamiento cuando no hay datos) | "Sin resultados... 200 OK con lista vacia y estado vacio en interfaz" | La refinada evita el limbo: no confunde vacio con error, y define respuesta y UX para ese caso. |
| (Sin contrato de respuesta detallado) | JSON con `items`, `total`, `id`, `nombre`, `pais`, `codigoPais`, `activa` | Se pasa de "mostrar lista" a "asi luce la lista". Queda modelo de datos verificable para backend y frontend. |
| (Sin control de acceso explicito) | "Solo usuarios autenticados..." + escenario `401 Unauthorized` | Se vuelve explicito el borde de seguridad: quien consulta y que pasa cuando la sesion no existe. |

## HU_NOTIFICACIONES: Recordatorios de fin de reserva y vencimiento

| HU original | HU refinada por la instruccion | Diferencias detectadas |
|---|---|---|
| Documento amplio con "Contexto y proposito", "Trazabilidad tecnica", "Casos de prueba existentes y recomendados". | Version resumida y guiada por "Historia", "Flujo funcional", "Reglas", "Gherkin", "NFR", "INVEST". | La refinada recorta secciones enciclopedicas y deja un camino mas directo de implementacion/verificacion. Menos inventario, mas ruta. |
| En original: detalle de consumo RabbitMQ + nombres de eventos + limpieza por cancelacion/devolucion + purga > 1 dia | En refinada: mismo flujo en 7 pasos, con banderas `hito_15m_enviado`, `hito_5m_enviado`, `hito_overdue_10m_enviado` | La mecanica se conserva, pero en la refinada aparece mas explicita la logica de estado por banderas para evitar reenvios. |
| Original incluye trazabilidad a rutas de codigo (`ReservationReminderApplicationService.java`, `useReminderAlerts.ts`, etc.) | Refinada no incluye rutas de archivos concretos | Se pierde detalle de "donde vive" en el repo, y se gana foco en "como funciona" a nivel HU. |
| Original trae "Casos de prueba existentes" + "E2E recomendados" por archivo/test suite | Refinada trae Gherkin y ejemplo temporal concreto (`endAt = 2026-03-11T15:00:00Z`) | La refinada no enumera suites reales, pero agrega ejemplo de reloj que aterriza la validacion funcional minuto a minuto. |
| Original: NFR con observabilidad y limitacion de estado en memoria (`ConcurrentHashMap`) | Refinada: NFR con criterio de desviacion `<= 30s` y guia de medicion | Se mueve el lente: menos detalle de implementacion interna, mas criterio operativo medible para validar comportamiento. |
| Original define frontend en `MyReservationsPage` + `ReminderAlertBanner` y acciones de descartar/limpiar | Refinada mantiene "banner en interfaz" y escenario de visualizacion; no enumera componentes por nombre de archivo | Se conserva experiencia del usuario, pero se aligera la referencia tecnica fina del frontend. |
