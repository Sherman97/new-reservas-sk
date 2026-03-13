Plantilla para Diligenciar Contexto de Negocio

1. Descripcion del Proyecto:
- Nombre del Proyecto: Reservas SK
- Objetivo del Proyecto: Permitir a los usuarios reservar espacios y equipos de forma segura, con informacion actualizada y control del ciclo de la reserva.

2. Flujos Criticos del Negocio:
- Principales Flujos de Trabajo:
    - Un visitante se registra, inicia sesion y accede al sistema como usuario autenticado.
    - El administrador mantiene el catalogo de ciudades, espacios y equipos.
    - El usuario consulta disponibilidad de un espacio en un rango de tiempo.
    - El usuario crea una reserva (con equipos opcionales) y el sistema la confirma si cumple reglas.
    - El usuario consulta sus reservas, revisa detalle y puede cancelar cuando aplica.
    - El sistema publica cambios en tiempo real para mantener vistas sincronizadas.
- Modulos o Funcionalidades Criticas:
    - Autenticacion y sesion (registro, login, perfil, cierre de sesion).
    - Catalogo de ubicaciones (ciudades y espacios).
    - Catalogo de inventario (equipos y estado).
    - Reservas (disponibilidad, creacion, consulta, cancelacion).
    - Eventos en tiempo real (mensajeria y WebSocket).
    - Dashboard y Mis Reservas como experiencia principal de usuario.

3. Reglas de Negocio y Restricciones:
- Reglas de Negocio Relevantes:
    - El correo del usuario es unico.
    - Solo usuarios autenticados acceden a rutas protegidas.
    - Ciudad y espacio deben mantener reglas de unicidad del catalogo.
    - No se elimina ciudad/espacio/equipo si afecta reservas activas.
    - Una reserva requiere inicio menor a fin y no puede solaparse con otra activa del mismo espacio.
    - Equipos asociados a la reserva deben existir, estar disponibles y pertenecer a la ciudad del espacio.
    - Una cancelacion valida cambia el estado y libera recursos.
- Regulaciones o Normativas:
    - Aplicar buenas practicas de seguridad, control de acceso y proteccion de datos de usuario.

4. Perfiles de Usuario y Roles:
- Perfiles o Roles de Usuario en el Sistema:
    - Visitante.
    - Usuario autenticado.
    - Administrador.
- Permisos y Limitaciones de Cada Perfil:
    - Visitante: puede registrarse e iniciar sesion.
    - Usuario autenticado: puede consultar, crear y cancelar sus reservas; no administra catalogo.
    - Administrador: gestiona ciudades, espacios y equipos con restricciones de negocio.

5. Condiciones del Entorno Tecnico:
- Plataformas Soportadas:
    - Aplicacion web.
    - Backend de microservicios.
- Tecnologias o Integraciones Clave:
    - API Gateway, servicios de autenticacion, reservas, ubicaciones, inventario y notificaciones.
    - JWT para seguridad.
    - MariaDB y Liquibase para persistencia.
    - RabbitMQ y WebSocket STOMP para eventos en tiempo real.

6. Casos Especiales o Excepciones (Opcional):
- Escenarios Alternos o Excepciones que Deben Considerarse:
    - Si el token es invalido o expira, el usuario debe volver a iniciar sesion.
    - Si no hay disponibilidad, no se crea la reserva.
    - Si los equipos no cumplen reglas, la reserva se rechaza.
    - Si el estado no permite cancelar, la operacion se rechaza.
