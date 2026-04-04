# Sistema de Reservas de Cine

REST API para la gestión de reservas de asientos en un cine, desarrollada como proyecto de aprendizaje de Java backend.

El foco principal es la implementación de **Pessimistic Locking** para garantizar consistencia en reservas concurrentes — si dos usuarios intentan reservar el mismo asiento al mismo tiempo, solo uno lo logra. Además incluye autenticación JWT con refresh tokens, rate limiting por IP, observabilidad con Prometheus, validaciones con Bean Validation, un flujo de reserva temporal con expiración automática y un patrón de excepciones escalable.

## Stack

- Java 21
- Spring Boot 4
- Hibernate / JPA
- PostgreSQL
- Redis
- Docker / Docker Compose

## Dependencias principales

| Dependencia | Uso |
|-------------|-----|
| `spring-boot-starter-data-jpa` | ORM y acceso a datos con Hibernate |
| `spring-boot-starter-web` | API REST con Spring MVC |
| `spring-boot-starter-security` | Seguridad y autenticación |
| `spring-boot-starter-validation` | Validaciones con Bean Validation / Jakarta |
| `spring-boot-starter-data-redis` | Caché y blacklist de tokens con Redis |
| `spring-boot-starter-actuator` | Endpoints de salud y métricas |
| `micrometer-registry-prometheus` | Exposición de métricas para Prometheus |
| `jjwt` (0.12.6) | Generación y validación de JWT |
| `bucket4j-core` (8.10.1) | Rate limiting por IP |
| `postgresql` | Driver JDBC para PostgreSQL |

## Arquitectura

El proyecto sigue una arquitectura en capas estándar:

    Controller → Service → Repository → PostgreSQL

Con DTOs (Records) como filtro entre el Controller y el Service para controlar qué datos entran y salen de la API, y un `GlobalExceptionHandler` que centraliza el manejo de errores devolviendo respuestas limpias con los códigos HTTP correctos.

Las excepciones de negocio se manejan con un patrón escalable — una única clase `AppException` con un enum `ErrorCode` que define el tipo de error y el status HTTP. Esto evita tener una clase por cada error a medida que el proyecto crece.

## Seguridad

La autenticación usa **JWT stateless** con **refresh tokens**. El flujo completo es:

1. El usuario se registra o hace login y recibe un `accessToken` (corta duración) y un `refreshToken` (larga duración).
2. Cada request incluye el `accessToken` en el header `Authorization: Bearer <token>`.
3. Cuando el access token expira, se puede renovar con el endpoint `/auth/refresh`.
4. Al hacer logout, el token se agrega a una **blacklist en Redis** para invalidarlo antes de que expire naturalmente.

El control de acceso está basado en roles (`ROLE_ADMIN`, `ROLE_USER`):

- Crear y eliminar salas y funciones: solo `ROLE_ADMIN`
- Reservar y confirmar asientos: cualquier usuario autenticado
- Ver salas, funciones y asientos: cualquier usuario autenticado

La configuración incluye también un **RateLimitFilter** implementado con **Bucket4j** que limita cada IP a 10 requests por minuto, devolviendo HTTP 429 cuando se supera el límite.

## Observabilidad

La aplicación expone métricas a través de **Spring Boot Actuator** y **Micrometer**, con un endpoint compatible con **Prometheus**:

- `GET /actuator/health` — estado de la aplicación
- `GET /actuator/metrics` — métricas internas
- `GET /actuator/prometheus` — métricas en formato Prometheus

El archivo `prometheus.yml` incluido en el proyecto configura el scraping contra `host.docker.internal:8080` con un intervalo de 15 segundos, listo para conectarse a un stack de Prometheus + Grafana.

## Validaciones

Los DTOs usan anotaciones de **Bean Validation** (Jakarta) para validar los datos de entrada. Errores de validación son capturados por el `GlobalExceptionHandler` y devueltos con HTTP 400.

Además de las validaciones de campos, se aplican reglas de negocio a nivel de servicio:

- No se pueden crear dos funciones en la misma sala si sus horarios se solapan (se valida que la nueva función empiece al menos 10 minutos después de que termine la anterior).
- No se puede reservar un asiento en estado `RESERVADO_TEMP` o `CONFIRMADO`.
- No se puede confirmar una reserva expirada.

## Flujo de reserva temporal

Los asientos tienen tres estados: `DISPONIBLE`, `RESERVADO_TEMP` y `CONFIRMADO`.

Cuando un usuario elige un asiento, este pasa a `RESERVADO_TEMP` con una expiración de 10 minutos. Durante ese tiempo nadie más puede tomarlo. Si el usuario confirma el pago, pasa a `CONFIRMADO`. Si no confirma en 10 minutos, un job automático (`AsientoExpirationJob`) lo vuelve a `DISPONIBLE`.

El bloqueo durante la reserva utiliza **Pessimistic Locking** (`PESSIMISTIC_WRITE`) a nivel de base de datos, garantizando que si dos usuarios intentan reservar el mismo asiento de forma concurrente, solo uno lo logrará.

## Tests

El proyecto tiene cobertura con **JUnit 5** y **Mockito**. Los tests corren sin levantar el contexto de Spring (usando `@ExtendWith(MockitoExtension.class)`), lo que los hace rápidos y aislados.

Casos cubiertos:

- `ReservaService` — reservar con función inexistente, asiento inexistente, asiento confirmado, asiento reservado temporalmente vigente y flujo exitoso; confirmar reserva con reserva inexistente, asiento expirado y flujo exitoso
- `SalaService` — eliminar sala inexistente, eliminar sala existente y creación con generación automática de asientos
- `FuncionService` — sala inexistente, solapamiento de horarios y flujo exitoso con DTO de respuesta correcto
- `AsientoExpirationJob` — liberación de asientos vencidos y caso sin vencidos

## Cómo levantar el proyecto

### Opción A — Docker Compose (recomendado)

Levanta PostgreSQL, Redis y Prometheus de una sola vez:

```bash
docker compose up -d
```

Luego ejecutar `ReservasCineApplication.java` desde IntelliJ.

### Opción B — Manual

**1. Levantar PostgreSQL:**

```bash
docker run --name reservas-db \
  -e POSTGRES_PASSWORD=cine123 \
  -e POSTGRES_DB=reservasdb \
  -e POSTGRES_USER=cine \
  -p 5432:5432 -d postgres
```

**2. Levantar Redis:**

```bash
docker run --name reservas-redis \
  -p 6379:6379 -d redis
```

**3. Configurar timezone en IntelliJ:**

En **Run → Edit Configurations → VM Options** agregar:

```
-Duser.timezone=UTC
```

**4. Correr la aplicación:**

Ejecutar `ReservasCineApplication.java` desde IntelliJ. Las tablas se crean automáticamente gracias a `ddl-auto=update`.

## Endpoints

### Autenticación

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/auth/register` | No | Registrar usuario |
| POST | `/auth/login` | No | Login, devuelve access + refresh token |
| POST | `/auth/refresh` | No | Renovar access token |
| POST | `/auth/logout` | Sí | Invalidar token activo |

### Salas

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| GET | `/salas` | USER | Listar salas |
| POST | `/salas` | ADMIN | Crear sala (genera asientos automáticamente) |
| DELETE | `/salas/{id}` | ADMIN | Eliminar sala y sus asientos |

### Funciones

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| GET | `/funciones` | USER | Listar funciones |
| POST | `/funciones` | ADMIN | Crear función |

### Asientos

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| GET | `/asientos/sala/{salaId}` | USER | Ver asientos de una sala |

### Reservas

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| GET | `/reservas` | USER | Listar reservas |
| POST | `/reservas` | USER | Crear reserva (bloquea asiento temporalmente) |
| POST | `/reservas/{id}/confirmar` | USER | Confirmar reserva (pasa asiento a CONFIRMADO) |

## Ejemplos de uso

**Registrarse:**

```bash
POST /auth/register
{
  "email": "tomi@mail.com",
  "password": "1234",
  "rol": "ROLE_ADMIN"
}
```

**Login:**

```bash
POST /auth/login
{
  "email": "tomi@mail.com",
  "password": "1234"
}
```

Respuesta:
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ..."
}
```

**Crear una sala** (requiere `ROLE_ADMIN`, header `Authorization: Bearer <token>`):

```bash
POST /salas
{
  "nombre": "Sala 1",
  "capacidad": 100
}
```

Respuesta:
```json
{
  "id": 1,
  "nombre": "Sala 1",
  "capacidad": 100
}
```

**Crear una función** (requiere `ROLE_ADMIN`):

```bash
POST /funciones
{
  "salaId": 1,
  "pelicula": "Inception",
  "horario": "2026-03-30T20:00:00",
  "duracionMinutos": 120
}
```

Respuesta:
```json
{
  "id": 1,
  "pelicula": "Inception",
  "horario": "2026-03-30T20:00",
  "duracionMinutos": 120,
  "nombreSala": "Sala 1"
}
```

**Reservar un asiento:**

```bash
POST /reservas
{
  "nombreUsuario": "tomi",
  "funcionId": 1,
  "asientoId": 5
}
```

**Confirmar la reserva** (dentro de los 10 minutos):

```bash
POST /reservas/1/confirmar
```

Respuesta:
```json
{
  "id": 1,
  "nombreUsuario": "tomi",
  "pelicula": "Inception",
  "horario": "2026-03-30T20:00",
  "numeroAsiento": 5
}
```

## Mejoras en progreso

- Docker Compose completo con todos los servicios (app, PostgreSQL, Redis, Prometheus)
- Migración a arquitectura de microservicios (separar el servicio de autenticación, reservas y catálogo en servicios independientes con comunicación vía REST o mensajería)
- Swagger / OpenAPI para documentación interactiva
- Rate limiting por usuario autenticado además de por IP