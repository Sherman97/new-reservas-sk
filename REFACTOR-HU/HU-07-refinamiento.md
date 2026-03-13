# HU-07 (Refinada): Listar ciudades

## Historia de usuario
Como usuario autenticado, quiero consultar el catálogo de ciudades disponibles, con su país asociado, para elegir una ubicación al buscar espacios o crear reservas.

## Alcance funcional
La funcionalidad expone y presenta una lista de ciudades desde el catálogo de ubicaciones para consumo en dashboard y flujos de reserva.

## Reglas de negocio explícitas
1. Solo usuarios autenticados pueden consultar la lista.
2. Por defecto se muestran solo ciudades activas.
3. La lista se entrega ordenada alfabéticamente por `nombre` (ascendente).
4. El texto de ciudad y país se presenta en español.
5. Si no existen ciudades activas, se devuelve lista vacía sin error.
6. La carga de la lista se ejecuta al entrar al dashboard y puede refrescarse manualmente.
7. Cambios del catálogo deben reflejarse en un máximo de 60 segundos mediante recarga o sincronización del cliente.

## Criterios de aceptación (Given / When / Then)
1. **Listado exitoso**
   - Given un usuario autenticado
   - When accede al dashboard
   - Then se consulta `GET /locations/cities` y se muestra la lista de ciudades activas con su país.

2. **Sin resultados**
   - Given un usuario autenticado
   - When no existen ciudades activas
   - Then la API responde `200 OK` con lista vacía y la interfaz muestra estado vacío.

3. **Orden de resultados**
   - Given ciudades activas registradas
   - When se consulta el endpoint
   - Then la respuesta llega ordenada alfabéticamente por nombre de ciudad.

4. **Error de servicio**
   - Given un usuario autenticado
   - When el servicio de ubicaciones falla o no está disponible
   - Then la interfaz muestra mensaje de error y opción de reintento.

5. **No autenticado**
   - Given un usuario sin sesión válida
   - When intenta consultar el listado
   - Then la API responde `401 Unauthorized`.

## Contrato de respuesta
### Response 200
```json
{
  "items": [
    {
      "id": "uuid",
      "nombre": "Bogota",
      "pais": "Colombia",
      "codigoPais": "CO",
      "activa": true
    }
  ],
  "total": 1
}
```

### Errores esperados
- `401 Unauthorized`: sesión inválida o ausente.
- `503 Service Unavailable`: dependencia no disponible.

## Comportamiento en dashboard
1. Carga inicial automática al abrir dashboard.
2. Estado de carga visible mientras responde el endpoint.
3. Estado vacío con mensaje funcional cuando `items = []`.
4. Estado de error con botón de reintentar.
5. Si el rol es administrador, la visualización base es igual; la diferencia de capacidades queda fuera de esta HU.

## Criterios INVEST (evaluación)
- **Independiente:** parcialmente, depende de existencia del catálogo de ciudades.
- **Negociable:** sí, admite ajustes de orden, paginación, filtros y actualización.
- **Valiosa:** alta, habilita el paso de selección geográfica en reservas.
- **Estimable:** sí, al definir alcance de datos, errores y comportamiento de UI.
- **Pequeña:** parcialmente, se mantiene pequeña si no incluye filtros avanzados.
- **Testeable:** sí, con escenarios de éxito, vacío, autenticación y falla de servicio.

## Preguntas de cierre para refinamiento con PO
1. ¿La respuesta debe incluir solo ciudades activas o también inactivas para roles específicos?
2. ¿Se requiere paginación desde esta HU o basta lista completa?
3. ¿Debe existir búsqueda por texto (ciudad/país) en este alcance?
4. ¿El país se muestra como nombre, código ISO o ambos?
5. ¿La actualización del catálogo debe ser en tiempo real (push) o por recarga periódica?
6. ¿Hay umbral de performance objetivo (por ejemplo, respuesta < 300 ms con 5.000 ciudades)?
7. ¿Se requieren métricas de uso y auditoría de consultas?

## Datos técnicos
- **Endpoint:** `GET /locations/cities`
- **Servicio:** `locations-service`
