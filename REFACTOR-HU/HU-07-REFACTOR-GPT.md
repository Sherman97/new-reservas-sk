# HU-07: Listar ciudades

## Historia de usuario
Como Colaborador autenticado, quiero ver la lista de Ciudades disponibles, para seleccionar una Ubicación al buscar Espacios de trabajo o crear una Reserva.

## Objetivo funcional
Exponer en el Dashboard una lista confiable de Ciudades del Catálogo de Ubicaciones para habilitar la selección de Ubicación en los flujos de búsqueda y Reserva.

## Alcance
- Consultar Ciudades desde `locations-service`.
- Mostrar lista con datos mínimos requeridos para selección de Ubicación.
- Manejar estados de carga, vacío y error.
- Definir orden por defecto de la lista.

## Reglas de negocio refinadas
1. Solo usuarios autenticados pueden consultar la lista de Ciudades.
2. La lista incluye, como mínimo, `id`, `nombre` y `pais` de cada Ciudad.
3. La lista debe mostrarse ordenada alfabéticamente por `nombre` de Ciudad.
4. Si no existen Ciudades registradas, se debe mostrar estado vacío con mensaje funcional.
5. Si falla la consulta al Servicio de Ubicaciones, se debe mostrar mensaje de error controlado sin exponer detalles técnicos sensibles.
6. La respuesta del backend debe ser consistente con el Catálogo de Ubicaciones activo.

## Criterios de aceptación (Given / When / Then)
1. Carga exitosa de ciudades
   Given un Colaborador autenticado en Dashboard
   When el sistema consulta `GET /locations/cities`
   Then muestra la lista de Ciudades disponibles con `id`, `nombre` y `pais`
   And la lista aparece ordenada alfabéticamente por `nombre`.

2. Estado vacío
   Given un Colaborador autenticado
   When no existen Ciudades registradas
   Then el sistema muestra el mensaje `No hay ciudades disponibles`
   And no muestra error técnico.

3. Error del servicio de ubicaciones
   Given un Colaborador autenticado
   When la consulta de Ciudades falla por error del backend o integración
   Then el sistema muestra un mensaje de error al usuario
   And permite reintentar la carga.

4. Acceso no autenticado
   Given un usuario sin sesión válida
   When intenta acceder al Dashboard y consultar Ciudades
   Then el sistema rechaza la operación según política de seguridad
   And redirige al flujo de autenticación.

## Contrato API base
### Endpoint
`GET /locations/cities`

### Servicio
`locations-service`

### Tabla de referencia
`cities`

### Response 200 (ejemplo)
```json
{
  "ok": true,
  "data": [
    {
      "id": 1,
      "name": "Bogota",
      "country": "Colombia",
      "createdAt": "2026-03-13T00:00:00Z",
      "updatedAt": "2026-03-13T00:00:00Z"
    },
    {
      "id": 2,
      "name": "Medellin",
      "country": "Colombia",
      "createdAt": "2026-03-13T00:00:00Z",
      "updatedAt": "2026-03-13T00:00:00Z"
    }
  ],
  "message": null,
  "errorCode": null
}
```

### Errores esperados
- `401 Unauthorized`: token ausente, inválido o expirado.
- `403 Forbidden`: usuario autenticado sin permisos para el recurso (si aplica por política futura).
- `500 Internal Server Error`: falla inesperada del servicio.

## Claridad y ambigüedades
### Elementos claros
- El objetivo funcional de la historia es entendible y alineado con selección de Ubicación.
- Se identifica endpoint y servicio responsable.

### Ambigüedades identificadas
- No se definía comportamiento ante lista vacía.
- No se definía manejo de errores del servicio.
- No se definía orden o filtros de presentación.
- No se aclaraban datos mínimos adicionales para selección (`id`).
- No se definía actualización de lista durante la sesión.
- No se definían criterios de accesibilidad/formato.

### Recomendaciones
- Mantener estado vacío explícito (`No hay ciudades disponibles`).
- Mantener manejo de error controlado con opción de reintento.
- Ordenar por nombre de Ciudad como comportamiento por defecto.
- Confirmar necesidad de filtros/búsqueda y paginación para catálogos grandes.
- Validar si se requiere refresco en tiempo real cuando cambie el Catálogo de Ubicaciones.
- Confirmar requisitos de accesibilidad para Dashboard.

## Criterios INVEST
- **Independiente (cumple parcialmente):** depende de autenticación y disponibilidad del Catálogo de Ubicaciones.
- **Negociable (cumple):** se puede ajustar forma de presentación, datos mostrados y capacidades de filtrado.
- **Valiosa (cumple):** habilita selección de Ubicación en flujos críticos de búsqueda y Reserva.
- **Estimable (cumple parcialmente):** requiere cerrar ambigüedades de vacíos, errores, filtros y actualización.
- **Pequeña (cumple):** alcance acotado a listar Ciudades; puede fragmentarse si incluye filtros/paginación.
- **Testeable (cumple parcialmente):** es testeable en éxito, pero necesita criterios cerrados para vacíos, error y UX de presentación.

## Coherencia con el proyecto
- Alineada con el objetivo de Reservas SK y el módulo crítico de Catálogo de Ubicaciones.
- Soporta el flujo de consulta/selección previo a crear Reservas.
- Es consistente con arquitectura de microservicios, JWT y operación web.
- Debe confirmarse si en el futuro existirá visibilidad por rol/perfil sobre Ciudades.
- Debe definirse si habrá actualización en tiempo real al cambiar Catálogo de Ubicaciones.

## Preguntas para refinamiento
1. ¿Qué debe suceder exactamente si no hay Ciudades registradas?
2. ¿La lista debe ordenarse por nombre, país u otro criterio?
3. ¿Se requiere búsqueda o filtrado desde Dashboard?
4. ¿Qué comportamiento de reintento se espera ante fallas del servicio?
5. ¿La lista debe actualizarse automáticamente si cambia el Catálogo de Ubicaciones durante la sesión?
6. ¿Qué campos son obligatorios en la respuesta además de nombre y país?
7. ¿Hay límite de resultados o necesidad de paginación?
8. ¿Existen requisitos de accesibilidad o formato visual específico?
9. ¿La visualización de Ciudades depende de perfil/rol en escenarios futuros?
10. ¿Debe auditarse la consulta de Ciudades por trazabilidad o seguridad?

## Resumen del análisis
La HU-07 está bien orientada al negocio y al flujo de Reservas, pero requería definición explícita de estados vacíos, errores y orden de presentación para mejorar estimación y pruebas. Con este refinamiento se cierra el comportamiento base de implementación y se deja un conjunto de preguntas para cerrar decisiones de producto y arquitectura antes de desarrollo.
