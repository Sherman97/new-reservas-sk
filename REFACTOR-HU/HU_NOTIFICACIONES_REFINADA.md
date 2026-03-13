### Historia de Usuario: Notificaciones de Proximidad y Vencimiento de Reserva

#### Título:
Notificaciones de Proximidad y Vencimiento de Reserva

#### Descripción:
Esta historia de usuario documenta la funcionalidad de notificaciones de proximidad y vencimiento para reservas activas. Proporciona alertas a los usuarios sobre la finalización inminente de sus reservas y el vencimiento de la devolución, asegurando así la puntualidad en la entrega y devolución del espacio.

#### Rol, Objetivo y Beneficio:
- **Rol:** Usuario autenticado que ha creado una reserva.
- **Objetivo:** Recibir recordatorios en tiempo real cuando faltan 15 y 5 minutos para finalizar su reserva, y una alerta si pasan 10 minutos después del fin sin devolución.
- **Beneficio:** Reducir retrasos en entrega/devolución y mejorar el cumplimiento operativo.

#### Alcance Funcional:
- **Backend:** Consume eventos RabbitMQ de reservas y publica notificaciones a través de WebSocket.
- **Frontend:** Se suscribe a eventos WebSocket y muestra alertas en tiempo real.

#### Reglas de Negocio:
- Se genera alerta de 15 minutos cuando faltan 15 minutos para finalizar la reserva.
- Se genera alerta de 5 minutos cuando faltan 5 minutos.
- Se genera alerta de vencimiento cuando han pasado 10 minutos desde la finalización sin devolución.
- Cada tipo de alerta se emite una sola vez por reserva.
- Una reserva cancelada o devuelta elimina su estado de recordatorios.
- Si el payload de evento no contiene información válida, no se agenda recordatorio.
- Los tiempos se calculan en base a la hora UTC.

#### Criterios de Aceptación (Gherkin):
```gherkin
Característica: Notificaciones de fin de reserva

  Escenario: Programar recordatorios al crear reserva
    Dado un evento "bookings.reservation.created" con reservationId, userId, spaceId y endAt válidos
    Cuando el notifications-service procesa el evento
    Entonces la reserva queda registrada para evaluación de recordatorios

  Escenario: Envío de recordatorio 15 minutos antes
    Dada una reserva registrada con endAt
    Y faltan 15 minutos o menos para endAt
    Cuando corre el scheduler de recordatorios
    Entonces se publica el evento "notifications.reservation.reminder.15m"
    Y la alerta incluye reservationId, userId, spaceId, endAt y minutesLeft=15

  Escenario: Envío de recordatorio 5 minutos antes
    Dada una reserva registrada con endAt
    Y faltan 5 minutos o menos para endAt
    Cuando corre el scheduler de recordatorios
    Entonces se publica el evento "notifications.reservation.reminder.5m"
    Y la alerta incluye reservationId, userId, spaceId, endAt y minutesLeft=5

  Escenario: Envío de alerta vencida 10 minutos después
    Dada una reserva registrada con endAt
    Y han pasado 10 minutos o más desde endAt
    Cuando corre el scheduler de recordatorios
    Entonces se publica el evento "notifications.reservation.reminder.overdue.10m"
    Y la alerta incluye reservationId, userId, spaceId, endAt y minutesOverdue=10

  Escenario: No duplicar recordatorios por tipo
    Dada una reserva para la que ya se emitió recordatorio de 15 minutos
    Cuando corre nuevamente el scheduler
    Entonces no se vuelve a publicar otro recordatorio de 15 minutos para esa reserva

  Escenario: Cancelación detiene recordatorios
    Dada una reserva registrada para recordatorios
    Cuando llega un evento "bookings.reservation.cancelled" de esa reserva
    Entonces se elimina el estado de recordatorios
    Y no se publican alertas futuras de esa reserva

  Escenario: Devolución detiene recordatorios
    Dada una reserva registrada para recordatorios
    Cuando llega un evento "bookings.reservation.returned" de esa reserva
    Entonces se elimina el estado de recordatorios
    Y no se publican alertas futuras de esa reserva

  Escenario: Frontend muestra alerta en tiempo real
    Dado que el usuario está en "Mis Reservas"
    Y existe conexión WebSocket activa al topic de recordatorios
    Cuando llega una notificación de recordatorio
    Entonces se visualiza en el banner con tipo, mensaje y número de reserva

  Escenario: Usuario descarta una alerta
    Dada una alerta visible en el banner
    Cuando el usuario pulsa "descartar"
    Entonces la alerta desaparece del listado visible

  Escenario: Usuario limpia todas las alertas
    Dadas múltiples alertas visibles
    Cuando el usuario pulsa "Limpiar todos"
    Entonces no quedan alertas visibles en el banner
```

#### Requisitos No Funcionales (NFR):
- **Tiempo de reacción:** Depende del scheduler (30s por defecto); tolerancia operativa de hasta un ciclo de scheduler.
- **Confiabilidad de conexión cliente:** WebSocket con reconnectDelay=5000ms, heartbeats de 10s.
- **Idempotencia funcional:** No se repite una misma alerta por reserva/tipo.
- **Resiliencia ante payload inválido:** Eventos inválidos se descartan sin romper el flujo.
- **Escalabilidad actual:** Estado de recordatorios en memoria (ConcurrentHashMap), sin persistencia distribuida.
- **Observabilidad:** Eventos y fallos quedan en logs de servicios; no hay auditoría persistente de notificaciones.

#### Casos de Prueba (Existentes y Recomendados):
- **Backend (automatizados existentes):**
    - ReservationReminderApplicationServiceTest: Envío de alertas 15m, 5m y vencida, limpieza por cancelación/devolución, descarte de payload inválido.
    - RabbitMqEventListenerTest: Delegación a broadcast + reminder service.

- **Frontend (automatizados existentes):**
    - useReminderAlerts.test.ts: Suscripción a 3 topics, creación de alertas por tipo, mensajes por defecto, deduplicación, dismiss y clear-all.
    - ReminderAlertBanner.test.jsx: Render por tipo (15m, 5m, overdue), estilos, iconos, badges, acciones de descartar/limpiar.

- **Casos E2E recomendados (faltantes):**
    - Crear reserva con endAt = now + 14m y validar alerta 15m en UI.
    - Esperar transición a ventana 5m y validar segunda alerta.
    - No devolver reserva y validar alerta overdue 10m.
    - Cancelar reserva antes de umbral y validar ausencia de nuevas alertas.
    - Devolver reserva antes de umbral y validar ausencia de nuevas alertas.

#### Trazabilidad Técnica:
- **Backend reminders:**
    - Backend/services/notifications-service/src/main/java/com/reservas/sk/notifications_service/application/service/ReservationReminderApplicationService.java
    - Backend/services/notifications-service/src/main/java/com/reservas/sk/notifications_service/adapters/in/rabbit/RabbitEventListenerAdapter.java
    - Backend/services/notifications-service/src/main/java/com/reservas/sk/notifications_service/adapters/out/websocket/StompWebSocketBroadcastAdapter.java
    - Backend/services/notifications-service/src/main/java/com/reservas/sk/notifications_service/infrastructure/config/WebSocketConfig.java
    - Backend/services/notifications-service/src/main/resources/application.properties

- **Frontend hook y banner:**
    - Frontend/src/core/adapters/hooks/useReminderAlerts.ts
    - Frontend/src/ui/components/reservations/ReminderAlertBanner.jsx
    - Frontend/src/ui/pages/reservations/MyReservationsPage.jsx

#### Criterio de Completitud de HU:
La HU se considera completa cuando:
- Los 3 hitos temporales (15m, 5m, +10m) están operativos y validados.
- Las reglas de no duplicación y limpieza por cancelación/devolución se cumplen.
- El frontend muestra alertas en tiempo real y permite gestión básica (descartar/limpiar).

#### Consideraciones Importantes:
- Asegúrate de que cada historia de usuario contenga título, descripción y criterios de aceptación. Si alguno de estos elementos falta, recomendar su creación.
- Asegúrate de que tu análisis sea exhaustivo y documenta tus hallazgos con claridad para facilitar la toma de decisiones y las futuras iteraciones del proyecto.

**