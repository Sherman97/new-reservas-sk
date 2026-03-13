# HU-06 (Refinada): Crear ciudad

## Historia de usuario
Cuando la malla territorial todavía tiene un hueco,  
el perfil administrador necesita trazar una ciudad nueva, enlazada a su país,  
para que los espacios de trabajo no queden flotando fuera del mapa operativo.

## Alcance funcional
La operación permite registrar una ciudad con `nombre` y `pais`, quedando disponible para asociación con espacios.

## Reglas de negocio explícitas
1. Solo roles con permiso administrativo pueden crear ciudades.
2. La dupla `nombre + pais` no puede repetirse.
3. `pais` debe validarse contra un catálogo vigente de países soportados por el sistema.
4. `nombre` debe cumplir validaciones de formato:
   - Longitud mínima: 2 caracteres.
   - Longitud máxima: 120 caracteres.
   - Caracteres permitidos: letras, espacios, guion y apóstrofo.
5. Si la ciudad se crea, debe emitirse `locations.city.created`.
6. Si el bus de eventos no está disponible:
   - La creación no se revierte.
   - El evento queda en reintento con mecanismo de outbox.
   - Se registra trazabilidad técnica del incidente.

## Criterios de aceptación (Given / When / Then)
1. **Alta exitosa**
   - Given un administrador autenticado y datos válidos
   - When envía `POST /locations/cities`
   - Then la API responde `201 Created` con la ciudad creada.

2. **País inexistente**
   - Given un administrador autenticado
   - When envía un `pais` fuera del catálogo
   - Then la API responde `422 Unprocessable Entity` con mensaje de validación.

3. **Ciudad duplicada**
   - Given que ya existe una ciudad con la misma dupla `nombre + pais`
   - When se intenta crear de nuevo
   - Then la API responde `409 Conflict` indicando duplicidad.

4. **Acceso no autorizado**
   - Given un usuario sin privilegios administrativos
   - When intenta crear ciudad
   - Then la API responde `403 Forbidden`.

5. **Evento emitido**
   - Given creación confirmada en base de datos
   - When finaliza la transacción
   - Then se publica `locations.city.created` con payload mínimo obligatorio.

## Contrato de entrada y salida
### Request
```json
{
  "nombre": "Bogota",
  "pais": "CO"
}
```

### Response 201
```json
{
  "id": "uuid",
  "nombre": "Bogota",
  "pais": "CO",
  "createdAt": "2026-03-11T14:00:00Z",
  "createdBy": "uuid"
}
```

### Errores esperados
- `403 Forbidden`: rol sin permisos.
- `409 Conflict`: ciudad ya registrada en el mismo país.
- `422 Unprocessable Entity`: validaciones de formato o país inválido.

## Evento de dominio
### Nombre
`locations.city.created`

### Payload mínimo
```json
{
  "eventId": "uuid",
  "occurredAt": "2026-03-11T14:00:00Z",
  "city": {
    "id": "uuid",
    "nombre": "Bogota",
    "pais": "CO"
  },
  "meta": {
    "createdBy": "uuid",
    "source": "locations-service"
  }
}
```

### Consumidores esperados (referenciales)
- Módulo de espacios (asociación geográfica).
- Módulo de búsqueda/filtros por ciudad.
- Módulo de auditoría.

## Criterios INVEST (evaluación)
- **Independiente:** sí, salvo dependencia explícita del catálogo de países.
- **Negociable:** sí, con margen en reglas de formato y política ante caída del bus.
- **Valiosa:** alta, habilita la base geográfica del dominio de reservas.
- **Estimable:** sí, al quedar definidos validaciones, errores y contrato de evento.
- **Pequeña:** sí, si el catálogo de países ya existe.
- **Testeable:** sí, con escenarios de éxito, duplicidad, permisos, validación y evento.

## Preguntas de cierre para refinamiento con PO
1. ¿El catálogo de países es interno, externo o sincronizado?
2. ¿Se requiere nombre de país (`Colombia`) además del código (`CO`) en la respuesta?
3. ¿Debe aplicarse normalización de tildes y mayúsculas para la unicidad?
4. ¿La emisión de evento debe ser sincrónica o estrictamente eventual vía outbox?
5. ¿Se exige bitácora funcional (quién, cuándo, antes/después) además de logs técnicos?

## Datos técnicos
- **Endpoint:** `POST /locations/cities`
- **Servicio:** `locations-service`
- **Tabla:** `cities`
