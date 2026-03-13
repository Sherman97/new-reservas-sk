# HU-06: Crear ciudad

## Historia de usuario
Como Administrador de Catálogo, quiero registrar una Ciudad con su nombre y país, para habilitar la asociación de Espacios de trabajo a una Ubicación dentro de Reservas SK.

## Objetivo funcional
Habilitar la gestión del Catálogo de Ubicaciones para que los Espacios de trabajo y las Reservas se relacionen con una Ciudad válida del dominio Reservas SK.

## Alcance
- Crear una Ciudad en el Servicio de Ubicaciones.
- Validar datos obligatorios y unicidad de Ciudad por País.
- Retornar la Ciudad creada con su identificador.
- Publicar el evento de dominio `locations.city.created`.

## Reglas de negocio refinadas
1. Solo usuarios autenticados con permiso de administración del Catálogo de Ubicaciones pueden crear Ciudades.
2. `nombre` y `pais` son obligatorios.
3. La combinación `nombre + pais` debe ser única en el Catálogo de Ubicaciones.
4. Si existe una Ciudad con la misma combinación `nombre + pais`, la operación se rechaza.
5. Si el alta de Ciudad es exitosa, se debe emitir `locations.city.created`.
6. La respuesta exitosa debe incluir el ID de la Ciudad creada.
7. La operación debe mantener trazabilidad de usuario autenticado (quién ejecutó la acción).

## Criterios de aceptación (Given / When / Then)
1. Creación exitosa de ciudad
   Given un Administrador de Catálogo autenticado con `nombre` y `pais` válidos
   When envía `POST /locations/cities`
   Then el sistema registra la Ciudad, responde `201 Created` y retorna el objeto con su `id`
   And se emite el evento `locations.city.created`.

2. Validación de obligatoriedad de país
   Given un Administrador de Catálogo autenticado
   When envía la solicitud sin `pais`
   Then el sistema responde `422 Unprocessable Entity`
   And muestra mensaje indicando que el país es obligatorio.

3. Validación de obligatoriedad de nombre
   Given un Administrador de Catálogo autenticado
   When envía la solicitud sin `nombre`
   Then el sistema responde `422 Unprocessable Entity`
   And muestra mensaje indicando que el nombre es obligatorio.

4. Unicidad de ciudad por país
   Given que ya existe una Ciudad con la combinación `nombre + pais`
   When se intenta registrar nuevamente la misma combinación
   Then el sistema responde `409 Conflict`
   And muestra mensaje indicando que la Ciudad ya está registrada.

5. Usuario sin permisos administrativos
   Given un usuario autenticado sin permisos para gestionar el Catálogo de Ubicaciones
   When intenta crear una Ciudad
   Then el sistema responde `403 Forbidden`.

6. Error técnico durante la creación
   Given una solicitud válida
   When ocurre un error inesperado en persistencia o integración
   Then el sistema responde con error controlado
   And muestra un mensaje de fallo en el alta de Ciudad sin exponer información sensible.

## Contrato API base
### Endpoint
`POST /locations/cities`

### Servicio
`locations-service`

### Tabla
`cities`

### Request mínimo
```json
{
  "name": "Bogota",
  "country": "Colombia"
}
```

### Response 201 (ejemplo)
```json
{
  "ok": true,
  "data": {
    "id": 1,
    "name": "Bogota",
    "country": "Colombia",
    "createdAt": "2026-03-13T00:00:00Z",
    "updatedAt": "2026-03-13T00:00:00Z"
  },
  "message": null,
  "errorCode": null
}
```

### Errores esperados
- `403 Forbidden`: usuario sin permisos de administración del catálogo.
- `409 Conflict`: Ciudad duplicada por combinación `nombre + pais`.
- `422 Unprocessable Entity`: faltan campos obligatorios o formato inválido.
- `500 Internal Server Error`: error inesperado en alta de Ciudad.

## Evento de dominio
### Nombre
`locations.city.created`

### Payload mínimo sugerido
```json
{
  "cityId": 1,
  "name": "Bogota",
  "country": "Colombia",
  "occurredAt": "2026-03-13T00:00:00Z"
}
```

## Alineación con contexto del proyecto
- Fortalece el módulo crítico de Catálogo de Ubicaciones.
- Soporta reglas de unicidad del catálogo (`Ciudad` y posterior asociación con `Espacio de trabajo`).
- Es consistente con arquitectura por microservicios, JWT y publicación de eventos para sincronización en tiempo real.
- Mantiene prácticas de seguridad y control de acceso para rutas protegidas.

## Criterios INVEST
- **Independiente:** Sí. El alta de Ciudad se puede implementar como capacidad del `locations-service` sin acoplarse al flujo transaccional de Reservas.
- **Negociable:** Sí. Se puede ajustar el detalle de validaciones (`pais` por catálogo ISO, formato de `nombre`, mensajes de error) durante refinamiento técnico.
- **Valiosa:** Sí. Aporta valor directo al negocio al habilitar el catálogo geográfico requerido para asociar Espacios y soportar búsquedas/Reservas por ubicación.
- **Estimable:** Sí. El alcance funcional, endpoint, reglas de unicidad, respuestas esperadas y emisión de evento están definidos.
- **Pequeña:** Sí. Es una unidad acotada (alta de Ciudad + validaciones + evento) y apta para un sprint.
- **Testeable:** Sí. Permite pruebas claras de éxito, obligatoriedad de campos, duplicidad, permisos y publicación de `locations.city.created`.

## Preguntas de refinamiento para PO/arquitectura
1. ¿El rol "administrador" existe explícitamente en el sistema o se implementará como permiso específico sobre catálogo de ubicaciones?
2. ¿Se valida `pais` contra catálogo ISO (por ejemplo `CO`) o se acepta texto libre?
3. ¿La unicidad `nombre + pais` debe evaluarse de forma case-insensitive y sin tildes?
4. ¿Qué payload exacto requiere el bus para `locations.city.created` y qué consumidores están acoplados?
5. ¿Cuál es el formato estándar de mensajes de error al Frontend para mantener consistencia con Dashboard y Mis Reservas?
