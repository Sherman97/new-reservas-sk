# Nueva historia de usuario: HU-06: Crear ciudad

**Título:** Crear ciudad

**Descripción:** Como administrador, quiero registrar una nueva ciudad con su nombre y país, para asociar espacios de trabajo a una ubicación geográfica y permitir que los usuarios vean y reserven espacios en ubicaciones específicas.

**Criterios de aceptación:**
1. Se envía el nombre y país de la ciudad.
2. La combinación nombre-país debe ser única.
3. Si el nombre o país de la ciudad ya existen, se debe mostrar un mensaje de error indicando que la ciudad ya está registrada.
4. Si el usuario no proporciona un país, se debe mostrar un mensaje de error indicando que el país es obligatorio.
5. Al crearse exitosamente, se emite el evento `locations.city.created`.
6. Se devuelve la ciudad creada con su ID.
7. Si hay un error en la creación de la ciudad, se debe mostrar un mensaje de error al usuario.

**Datos técnicos:**

**Endpoint:**
```
POST /locations/cities
```

**Servicio:**
```
locations-service
```

**Tabla:**
```
cities
```

**Preguntas para refinamiento:**
1. ¿Cómo se deben manejar los errores si el nombre o país de la ciudad ya existen?
2. ¿Qué sucede si el usuario intenta crear una ciudad sin proporcionar un país?
3. ¿Cómo se notifica al usuario sobre el éxito o fracaso de la creación de la ciudad?

**Keywords:** historia de usuario, creación de ciudad, criterios de aceptación, validación, asociación de espacios, refinamiento.
