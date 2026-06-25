# Notas sobre CORS (y otros ajustes de integración)

Documento técnico para el informe. Describe el problema de CORS entre el
frontend (React + Vite) y el backend (Spring Boot), cómo se resolvió, y un par
de ajustes adicionales de integración que surgieron al levantar todo junto.

---

## 1. El problema de CORS

### Contexto

- El **backend** corre en `http://localhost:8080`.
- El **frontend** (Vite) corre en `http://localhost:5173`.

Son **orígenes distintos** (distinto puerto = distinto origen, según la
*Same-Origin Policy* del navegador). Por lo tanto, cuando el código JavaScript
del frontend hace `fetch`/`axios` hacia `http://localhost:8080/api/...`, el
navegador aplica la política CORS (*Cross-Origin Resource Sharing*).

### Excepción / error concreto que se produce sin configurar CORS

Sin configuración de CORS en el backend, el navegador **bloquea la respuesta**
y en la consola del navegador aparece:

```
Access to XMLHttpRequest at 'http://localhost:8080/api/productos'
from origin 'http://localhost:5173' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

Detalles importantes de cómo se manifiesta:

- Para requests "simples" (`GET`), la request **sí llega** al backend y este
  responde 200, pero como la respuesta no trae el header
  `Access-Control-Allow-Origin`, **el navegador descarta la respuesta** y el
  `axios`/`fetch` falla con un error de red genérico (`Network Error`). En la
  pestaña *Network* se ve la request en estado `(failed)` / `CORS error`.
- Para requests con *preflight* (`POST`/`PUT`/`DELETE` con
  `Content-Type: application/json`), el navegador primero envía una request
  `OPTIONS` (preflight). Si el backend no la maneja, devuelve `403`/`401` o no
  incluye los headers `Access-Control-Allow-*`, y la request real **nunca se
  envía**.

> Nota de honestidad técnica: en este proyecto la configuración de CORS se hizo
> **de entrada** (antes de conectar el frontend), por lo que el error de arriba
> no se llegó a disparar en runtime. Se documenta el error esperado y la
> configuración que lo previene, que es lo que pide la consigna.

### La solución aplicada

Se configuró CORS de forma **global** en el backend mediante un
`WebMvcConfigurer`, en el archivo:

`backend/src/main/java/com/ecomarket/config/CorsConfig.java`

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${ecomarket.cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

Qué hace cada parte:

- `addMapping("/api/**")`: aplica CORS a todos los endpoints de la API.
- `allowedOrigins(...)`: habilita explícitamente el origen del frontend. Se
  contemplan **5173** (Vite, el que usamos) y **3000** (Create React App), por
  las dudas. El valor es configurable por la property
  `ecomarket.cors.allowed-origins` / variable de entorno `CORS_ORIGINS`.
- `allowedMethods(...)`: incluye `OPTIONS` para que el **preflight** funcione,
  además de los verbos que usa la API (`GET`, `POST`, `PUT`, `DELETE`).
- `allowedHeaders("*")`: permite el header `Content-Type: application/json`
  que envía axios.
- `allowCredentials(true)` + `maxAge(3600)`: permite credenciales y cachea el
  preflight 1 hora para no repetirlo en cada request.

### Verificación

Con el backend levantado, un preflight simulado devuelve los headers correctos:

```bash
curl -i -X OPTIONS http://localhost:8080/api/productos \
  -H 'Origin: http://localhost:5173' \
  -H 'Access-Control-Request-Method: POST' \
  -H 'Access-Control-Request-Headers: content-type'
```

Respuesta:

```
HTTP/1.1 200
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
Access-Control-Allow-Headers: content-type
Access-Control-Allow-Credentials: true
```

El header `Access-Control-Allow-Origin` confirma que el navegador aceptará las
respuestas del backend desde el frontend.

---

## 2. Otros ajustes de integración (no CORS)

Al levantar backend + frontend + MySQL juntos surgieron dos cuestiones que
también valen para el informe:

### 2.1. Conflicto de puerto de MySQL (3306 ocupado)

**Síntoma:** el backend fallaba al arrancar con:

```
java.sql.SQLException: Access denied for user 'ecomarket'@'localhost' (using password: YES)
... Unable to determine Dialect without JDBC metadata ...
```

**Causa:** la máquina ya tenía **un MySQL local escuchando en el puerto 3306**,
que "tapaba" al contenedor de Docker. El backend, al conectarse a
`localhost:3306`, pegaba contra el MySQL local (donde el usuario `ecomarket` no
existe) en lugar del de Docker.

**Solución:** se publicó el MySQL de Docker en el puerto **3307** del host
(`"3307:3306"` en `docker-compose.yml`) y se apuntó el backend a `localhost:3307`
por defecto. Así nunca choca con una instalación local previa de MySQL.

### 2.2. Usuario de MySQL y conexión sobre TCP

- Docker Desktop/OrbStack puede presentar la conexión TCP del host como
  proveniente de `localhost`, y MySQL no la hace coincidir con el usuario
  `ecomarket@'%'`. Para que un clon limpio funcione, el script
  `docker/mysql-init.sql` crea el usuario también para `'localhost'`.
- MySQL 8 usa por defecto el plugin `caching_sha2_password`. Al conectar por
  TCP sin TLS, Connector/J necesita `allowPublicKeyRetrieval=true&useSSL=false`
  en la URL JDBC; se agregaron a la URL por defecto.

### 2.3. Formato de fecha

El backend serializa `LocalDateTime` como ISO-8601
(`2026-06-25T15:15:28.414234`). El frontend lo parsea con `new Date(iso)` y lo
muestra formateado con `toLocaleString('es-AR', ...)` en
`src/utils/formato.js`. No hizo falta configuración extra de Jackson.

---

## Resumen

| Tema | Estado |
|------|--------|
| CORS frontend (5173) ↔ backend (8080) | Resuelto con `CorsConfig` global |
| Preflight `OPTIONS` | Habilitado (método permitido + `maxAge`) |
| Puerto MySQL en conflicto | Movido a 3307 en docker-compose |
| Auth MySQL sobre TCP | `allowPublicKeyRetrieval` + init SQL para `localhost` |
| Fechas | ISO del backend, formateadas en el frontend |
