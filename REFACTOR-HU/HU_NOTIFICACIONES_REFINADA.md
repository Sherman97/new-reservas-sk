# HU Notificaciones de Reserva (Lenguaje Ubicuo Mejorado)

## 1. Historia de Usuario
**Como** usuario autenticado con una reserva activa,  
**quiero** recibir alertas en la aplicacion cuando falten 15 y 5 minutos para terminar mi reserva, y otra alerta si pasan 10 minutos despues del fin sin devolucion/cierre,  
**para** entregar el espacio a tiempo y evitar incumplimientos.

## 2. Tipo de Notificacion y Canal
- Tipo: **notificacion in-app en tiempo real** (banner en interfaz).
- Transporte: **WebSocket STOMP**.
- No incluye (en este alcance): email, SMS, push mobile.

## 3. Flujo Funcional Detallado
1. Al llegar evento `bookings.reservation.created`, el sistema valida `reservationId`, `userId`, `spaceId`, `endAt`.
2. Si el evento es valido, registra estado temporal por reserva:
   - hito_15m_enviado = false
   - hito_5m_enviado = false
   - hito_overdue_10m_enviado = false
3. Un scheduler corre cada 30 segundos (configurable) y evalua cada reserva activa.
4. Si se cumple un umbral y su bandera esta en false, publica el evento de recordatorio y marca bandera en true.
5. El frontend suscrito a los topics muestra la alerta en "Mis Reservas".
6. Si llega `bookings.reservation.cancelled` o `bookings.reservation.returned`, elimina el estado temporal y no se emiten alertas futuras para esa reserva.
7. Limpieza tecnica: estados vencidos se purgan despues de 1 dia de `endAt`.

## 4. Reglas de Negocio
1. Recordatorio 15m: se emite cuando `now >= endAt - 15m`.
2. Recordatorio 5m: se emite cuando `now >= endAt - 5m`.
3. Vencimiento 10m: se emite cuando `now >= endAt + 10m`.
4. Cada tipo de alerta se emite maximo una vez por reserva.
5. Eventos con datos obligatorios faltantes se descartan sin agendar.
6. Calculo temporal con `Instant` (UTC).
7. En UI no se duplican alertas por `(reservationId, tipo)`.

## 5. Criterios de Aceptacion (Gherkin + ejemplo verificable)
```gherkin
Caracteristica: Recordatorios de fin de reserva

  Escenario: Agenda al crear reserva
    Dado un evento bookings.reservation.created valido
    Cuando notifications-service procesa el evento
    Entonces la reserva queda registrada para evaluacion de recordatorios

  Escenario: Envio de alerta 15 minutos antes
    Dada una reserva con endAt
    Cuando now es mayor o igual a endAt menos 15 minutos
    Entonces se publica notifications.reservation.reminder.15m una sola vez

  Escenario: Envio de alerta 5 minutos antes
    Dada una reserva con endAt
    Cuando now es mayor o igual a endAt menos 5 minutos
    Entonces se publica notifications.reservation.reminder.5m una sola vez

  Escenario: Envio de alerta 10 minutos vencida
    Dada una reserva con endAt
    Cuando now es mayor o igual a endAt mas 10 minutos
    Entonces se publica notifications.reservation.reminder.overdue.10m una sola vez

  Escenario: Cancelacion detiene alertas
    Dada una reserva registrada para recordatorios
    Cuando llega bookings.reservation.cancelled
    Entonces se elimina su estado temporal
    Y no se publican nuevas alertas de esa reserva

  Escenario: Devolucion detiene alertas
    Dada una reserva registrada para recordatorios
    Cuando llega bookings.reservation.returned
    Entonces se elimina su estado temporal
    Y no se publican nuevas alertas de esa reserva

  Escenario: Visualizacion en UI
    Dado un usuario en Mis Reservas con conexion STOMP activa
    Cuando llega un evento de recordatorio
    Entonces se muestra una alerta en el banner correspondiente
```

### Ejemplo concreto de validacion temporal
- `endAt = 2026-03-11T15:00:00Z`
- A las `14:45:00Z` o despues: debe existir alerta `15m`.
- A las `14:55:00Z` o despues: debe existir alerta `5m`.
- A las `15:10:00Z` o despues, sin devolucion: debe existir alerta `overdue.10m`.

## 6. Requisitos No Funcionales y Validacion
- Frecuencia de evaluacion: 30s por defecto.
- Latencia funcional esperada: la alerta puede tardar hasta 1 ciclo de scheduler.
- Reconexion cliente: `reconnectDelay=5000ms`.
- Robustez: payload invalido no rompe el proceso.
- Idempotencia: no duplicacion por reserva/tipo.

### Como validar rendimiento y respuesta
- Medir diferencia entre tiempo de umbral y tiempo de publicacion.
- Criterio objetivo: desviacion operativa <= 30s (configuracion por defecto).
- Ejecutar pruebas con multiples reservas simultaneas para verificar estabilidad del scheduler.

## 7. Coherencia e Integracion con el Sistema
- Se integra con:
  - `bookings-service` (origen de eventos de reserva),
  - `notifications-service` (procesamiento y broadcast),
  - `MyReservationsPage` + `ReminderAlertBanner` (consumo y visualizacion).
- Mantiene consistencia con el flujo global de eventos del sistema (RabbitMQ + STOMP).

## 8. Criterios INVEST (resumen)
- Independiente: si, se implementa como capacidad enfocada en recordatorios.
- Negociable: si, parametros de tiempo y canal pueden ajustarse.
- Valiosa: si, reduce retrasos de entrega.
- Estimable: si, con backend scheduler + eventos + UI.
- Pequena: si, alcance funcional delimitado.
- Testeable: si, con escenarios Gherkin, ejemplos temporales y metricas de latencia.
