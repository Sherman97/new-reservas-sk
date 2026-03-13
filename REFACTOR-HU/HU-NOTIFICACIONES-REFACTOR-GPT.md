# HU-NOTIFICACIONES: Notificaciones de proximidad y vencimiento de reserva

## Título
Notificaciones de proximidad y vencimiento de reserva.

## Descripción breve
Como Colaborador autenticado con una Reserva activa, quiero recibir alertas en tiempo real antes y después del fin de mi Reserva, para gestionar oportunamente la entrega/devolución del Espacio de trabajo y evitar incumplimientos operativos.

## Historia de usuario
Como Colaborador autenticado con una Reserva activa, quiero recibir alertas cuando falten 15 y 5 minutos para finalizar mi Reserva, y una alerta si pasan 10 minutos después del fin sin devolución, para gestionar oportunamente la entrega/devolución del Espacio de trabajo.

## Alcance funcional
- Alertar 15 minutos antes del fin de la Reserva.
- Alertar 5 minutos antes del fin de la Reserva.
- Alertar 10 minutos después del fin cuando no hay devolución/cierre de la Reserva.
- Mostrar alertas en Frontend en tiempo real por WebSocket.
- Permitir descartar alertas y limpiar listado visible.

## Reglas de negocio refinadas
1. Las alertas aplican solo a Reservas activas con datos válidos (`reservationId`, `userId`, `spaceId`, `endAt`).
2. Cada tipo de alerta se emite máximo una vez por Reserva (`15m`, `5m`, `overdue10m`).
3. Si la Reserva se cancela o devuelve, se detienen alertas futuras de esa Reserva.
4. El cálculo temporal en Backend usa UTC; Frontend presenta la hora en zona local del Colaborador.
5. Si el usuario está desconectado del WebSocket, el sistema debe reconectar y recuperar alertas nuevas cuando sea posible según estrategia definida.
6. El mensaje mostrado al usuario debe corresponder al tipo de alerta y tener fallback por defecto.

## Criterios de aceptación (Given / When / Then)
1. Recordatorio 15 minutos antes
   Given una Reserva activa registrada con `endAt` válido
   When faltan 15 minutos o menos para su finalización
   Then se emite una alerta tipo `15m`
   And el usuario la ve en tiempo real en "Mis Reservas".

2. Recordatorio 5 minutos antes
   Given una Reserva activa registrada con `endAt` válido
   When faltan 5 minutos o menos para su finalización
   Then se emite una alerta tipo `5m`
   And el usuario la ve en tiempo real en "Mis Reservas".

3. Alerta de vencimiento 10 minutos después
   Given una Reserva activa no devuelta
   When han pasado 10 minutos o más desde `endAt`
   Then se emite una alerta tipo `overdue10m`.

4. No duplicación de alertas
   Given una alerta ya emitida para una Reserva y tipo específico
   When corre de nuevo el scheduler
   Then no se vuelve a publicar la misma alerta.

5. Cancelación o devolución detiene alertas
   Given una Reserva con recordatorios programados
   When llega evento de cancelación o devolución
   Then se elimina su estado de recordatorios
   And no se generan nuevas alertas para esa Reserva.

6. Manejo de desconexión del cliente
   Given un usuario con desconexión temporal de WebSocket
   When se restablece la conexión
   Then el cliente se reconecta automáticamente
   And continúa recibiendo alertas vigentes según política definida.

## Ejemplos de mensajes por tipo
- `15m`: "Tu reserva termina en 15 minutos. Prepárate para la devolución del espacio."
- `5m`: "Tu reserva termina en 5 minutos. Realiza la devolución a tiempo."
- `overdue10m`: "Han pasado 10 minutos desde el fin de tu reserva y aún no registra devolución."
- `error-carga`: "No fue posible cargar notificaciones en este momento. Intenta nuevamente."

## Datos técnicos
### Eventos de entrada
- `bookings.reservation.created`
- `bookings.reservation.delivered`
- `bookings.reservation.cancelled`
- `bookings.reservation.returned`

### Eventos de salida
- `notifications.reservation.reminder.15m`
- `notifications.reservation.reminder.5m`
- `notifications.reservation.reminder.overdue.10m`

### Transporte
- RabbitMQ para integración entre servicios.
- WebSocket STOMP para entrega en tiempo real al Frontend.

## Claridad y ambigüedades
### Elementos claros
- El objetivo está bien definido: notificar fin y vencimiento de Reserva.
- Los hitos temporales están definidos: 15m, 5m y +10m.
- Los escenarios principales están cubiertos en aceptación.

### Ambigüedades identificadas
- En la versión original faltaba título explícito.
- Faltaba una descripción breve y concreta.
- No estaba cerrado el manejo de zona horaria de cara al usuario.
- No había ejemplos concretos de mensajes por tipo.
- Persistencia en memoria genera duda de escalabilidad en múltiples instancias del Servicio de Notificaciones.

### Recomendaciones
- Mantener título y descripción explícitos.
- Documentar UTC en Backend y visualización en hora local en Frontend.
- Mantener catálogo de mensajes por tipo (con fallback y localización futura).
- Definir estrategia distribuida para estado de recordatorios en producción.

## Criterios INVEST
- **Independiente (cumple):** puede desplegarse como mejora incremental, dependiendo solo de Reservas activas y eventos base.
- **Negociable (cumple parcialmente):** hitos temporales están definidos pero deberían ser configurables (`15m`, `5m`, `10m`).
- **Valiosa (cumple):** mejora cumplimiento operativo y experiencia del colaborador.
- **Estimable (cumple):** alcance y criterios permiten estimar implementación y pruebas.
- **Pequeña (cumple):** es una capacidad acotada, aunque escalable a nuevos canales.
- **Testeable (cumple):** los escenarios funcionales y técnicos son verificables con pruebas automáticas y E2E.

## Coherencia con el proyecto
- Alineada con Reservas SK al reforzar control del ciclo de Reserva y puntualidad de devolución.
- Coherente con stack definido (RabbitMQ + WebSocket + microservicios).
- Orientada al rol Colaborador, que es el usuario estándar actual.
- Debe resolverse la escalabilidad del estado en memoria para despliegues multi-instancia del Servicio de Notificaciones.
- Debe decidirse si se requiere auditoría persistente de alertas enviadas/vistas.

## Preguntas para refinamiento
1. ¿Los tiempos de alerta (`15m`, `5m`, `10m`) deben ser configurables por administración?
2. ¿Cómo se manejará la zona horaria en UI y qué hora verá exactamente el usuario?
3. ¿Qué mensajes exactos se deben mostrar por tipo y si habrá soporte multiidioma?
4. ¿Se debe persistir trazabilidad de alertas enviadas, vistas y descartadas?
5. ¿Qué política aplica ante desconexión WebSocket: reintento, backlog, o solo alertas nuevas?
6. ¿Se incluirán canales futuros (correo, push) y cómo impacta el diseño actual?
7. ¿Qué ocurre si la alerta se genera cuando el frontend está desconectado?
8. ¿Cómo se trata una Reserva de larga duración o escenarios recurrentes?
9. ¿Qué prioridad tendrán estas alertas frente a otras notificaciones del sistema?

## Resumen del análisis
La HU de Notificaciones está bien alineada al negocio y técnicamente consistente con el proyecto. El refinamiento agrega título, descripción operativa, manejo explícito de zona horaria, ejemplos de mensajes y criterios de aceptación reforzados para desconexión y no duplicación. Los principales puntos abiertos son configurabilidad de tiempos, escalabilidad en entornos distribuidos y necesidad de auditoría persistente.
