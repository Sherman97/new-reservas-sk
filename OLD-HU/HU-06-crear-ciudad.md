# HU-06: Crear ciudad

Como administrador, quiero registrar una nueva ciudad con su nombre y país,
para asociar espacios de trabajo a una ubicación geográfica.

## Criterios de aceptación:

Se envía nombre y país de la ciudad.
La combinación nombre-país debe ser única.
Al crearse exitosamente, se emite el evento locations.city.created.
Se devuelve la ciudad creada con su ID.
Datos técnicos:

## Endpoint: POST /locations/cities
## Servicio: locations-service
## Tabla: cities
