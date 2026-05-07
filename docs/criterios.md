# Planificación de Criterios para Sobresaliente

A continuación se detalla la planificación paso a paso para implementar los dos criterios solicitados para alcanzar la nota de sobresaliente en el proyecto de AISS.

## 1. Extensión del Modelo de Datos

**Objetivo:** Enriquecer el dominio de datos de **VideoMiner** (que actualmente cuenta con `Channel`, `Video`, `Comment`, `Caption` y `User`) para poder almacenar información adicional y más compleja proveniente de las APIs de video (como YouTube, Vimeo o Dailymotion).

**Pasos a seguir para la implementación:**

1. **Selección de la Extensión:** 
   Se analizarán los datos adicionales que proveen las APIs para incluirlos en el modelo. Algunas propuestas viables son:
   - **Estadísticas en `Video` y `Channel`:** Añadir propiedades como `viewCount` (reproducciones), `likeCount` (me gustas) al modelo `Video`, y `subscriberCount` (suscriptores) al `Channel`.
   - **Nuevas Entidades:** Implementar una nueva entidad como `Tag` (relación Many-to-Many con `Video`) o `Playlist` (relación One-to-Many con `Video`) para representar agrupaciones.

2. **Modificación de las Clases JPA (Directorio `model`):**
   - Actualizar los archivos Java (ej: `Video.java`, `Channel.java`) añadiendo los nuevos atributos.
   - Definir las anotaciones de persistencia de JPA (`@Column`, `@ManyToMany`, etc.) para que se reflejen correctamente en la base de datos H2.
   - Aplicar anotaciones de validación (por ejemplo, `@Min(0)` para contadores) y las correspondientes de Jackson (`@JsonProperty`) para el parseo JSON.
   - Generar getters, setters y actualizar constructores/toString.

3. **Adaptación de Repositorios y Controladores:**
   - Si se añade una entidad nueva (como `Tag`), se creará su propio `TagRepository` extendiendo de `JpaRepository`.
   - Se revisarán los controladores actuales para asegurar que los nuevos campos se gestionen adecuadamente en las peticiones (especialmente al realizar operaciones POST y PUT).

4. **Verificación de la Base de Datos y OpenAPI:**
   - Comprobar al iniciar la aplicación que Hibernate (`ddl-auto=update`) crea las nuevas columnas/tablas en H2.
   - Validar que Swagger UI (`/swagger-ui.html`) refleje los nuevos esquemas de datos de forma automática.

---

## 2. Uso de Autenticación mediante API Keys en VideoMiner

**Objetivo:** Proteger los endpoints de la API REST de VideoMiner para asegurar que únicamente clientes autorizados (como los microservicios adaptadores/mineros) puedan introducir o modificar datos en el sistema.

**Pasos a seguir para la implementación:**

1. **Definición del Mecanismo de Seguridad:**
   - Utilizaremos un esquema basado en un encabezado HTTP personalizado (ej. `Authorization: Bearer <API-KEY>` o un token `X-API-KEY`).
   - La clave (o claves) de acceso válida se definirá en el archivo de configuración `application.properties` (ej. `videominer.api.key=MiClaveSecreta123`) para facilitar su gestión.

2. **Creación de un Interceptor (Filtro de Peticiones):**
   - Se programará una nueva clase en un paquete como `aiss.videominer.security`.
   - Esta clase implementará la interfaz `HandlerInterceptor` de Spring.
   - En su método `preHandle`, se capturará la petición HTTP para comprobar la existencia del encabezado con la clave.
   - Si la clave es válida, se permitirá continuar el flujo (`return true`). Si es inválida o no existe, se interceptará la petición retornando un estado HTTP 401 (Unauthorized) o 403 (Forbidden).

3. **Configuración de Rutas Protegidas:**
   - Se creará una clase de configuración anotada con `@Configuration` que implemente `WebMvcConfigurer`.
   - Se sobreescribirá el método `addInterceptors` para registrar el interceptor previamente creado.
   - **Aplicación selectiva:** Se configurará el interceptor para que proteja, por ejemplo, todas las rutas del sistema, o preferiblemente solo aquellas que alteran el estado del sistema (peticiones POST, PUT, DELETE), dejando las consultas (GET) de acceso público o aplicando restricciones según convenga.

4. **Pruebas de Validación:**
   - Se probarán los endpoints desde Swagger y/o Postman verificando que fallen si no se provee la clave.
   - Se ajustarán (si procede) las pruebas unitarias/de integración de Spring Boot para inyectar la API Key y asegurar que los test siguen pasando correctamente.
