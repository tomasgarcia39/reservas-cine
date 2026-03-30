# Sistema de Reservas de Cine

REST API para la gestión de reservas de asientos en un cine, desarrollada como proyecto de aprendizaje de Java backend.

El foco principal es la implementación de **Pessimistic Locking** para garantizar consistencia en reservas concurrentes — si dos usuarios intentan reservar el mismo asiento al mismo tiempo, solo uno lo logra. Además incluye un flujo de reserva temporal con expiración automática, validaciones de negocio y un patrón de excepciones escalable.

## Stack

- Java 21
- Spring Boot 4
- Hibernate / JPA
- PostgreSQL 18
- Docker

## Arquitectura

El proyecto sigue una arquitectura en capas estándar:

    Controller → Service → Repository → PostgreSQL

Con DTOs (Records) como filtro entre el Controller y el Service para controlar qué datos entran y salen de la API, y un GlobalExceptionHandler que centraliza el manejo de errores devolviendo respuestas limpias con los códigos HTTP correctos.

Las excepciones de negocio se manejan con un patrón escalable — una única clase `AppException` con un enum `ErrorCode` que define el tipo de error y el status HTTP. Esto evita tener una clase por cada error a medida que el proyecto crece.

## Flujo de reserva temporal

Los asientos tienen tres estados: `DISPONIBLE`, `RESERVADO_TEMP` y `CONFIRMADO`.

Cuando un usuario elige un asiento, este pasa a `RESERVADO_TEMP` con una expiración de 10 minutos. Durante ese tiempo nadie más puede tomarlo. Si el usuario confirma el pago, pasa a `CONFIRMADO`. Si no confirma en 10 minutos, un job automático lo vuelve a `DISPONIBLE`.

## Cómo levantar el proyecto

### 1. Levantar PostgreSQL con Docker

    docker run --name reservas-db \
      -e POSTGRES_PASSWORD=cine123 \
      -e POSTGRES_DB=reservasdb \
      -e POSTGRES_USER=cine \
      -p 5432:5432 -d postgres

### 2. Configurar timezone en IntelliJ

En **Run → Edit Configurations → VM Options** agregar:

    -Duser.timezone=UTC

### 3. Correr la aplicación

Ejecutar `ReservasCineApplication.java` desde IntelliJ. Las tablas se crean automáticamente gracias a `ddl-auto=update`.

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/salas` | Listar salas |
| POST | `/salas` | Crear sala (genera asientos automáticamente) |
| DELETE | `/salas/{id}` | Eliminar sala y sus asientos |
| GET | `/asientos/sala/{salaId}` | Ver asientos de una sala |
| GET | `/funciones` | Listar funciones |
| POST | `/funciones` | Crear función |
| GET | `/reservas` | Listar reservas |
| POST | `/reservas` | Crear reserva (bloquea asiento temporalmente) |
| POST | `/reservas/{id}/confirmar` | Confirmar reserva (pasa asiento a CONFIRMADO) |

## Ejemplos de uso

**Crear una sala** (genera los asientos automáticamente):

    POST /salas
    {
      "nombre": "Sala 1",
      "capacidad": 100
    }

**Crear una función** (con duración en minutos):

    POST /funciones
    {
      "salaId": 1,
      "pelicula": "Inception",
      "horario": "2026-03-30T20:00:00",
      "duracionMinutos": 120
    }

No se pueden crear dos funciones en la misma sala si sus horarios se solapan — se valida que la nueva función empiece al menos 10 minutos después de que termine la anterior.

**Reservar un asiento:**

    POST /reservas
    {
      "nombreUsuario": "tomi",
      "funcionId": 1,
      "asientoId": 5
    }

**Confirmar la reserva** (dentro de los 10 minutos):

    POST /reservas/1/confirmar

**Respuesta exitosa:**

    {
      "id": 1,
      "nombreUsuario": "tomi",
      "pelicula": "Inception",
      "horario": "2026-03-30T20:00",
      "numeroAsiento": 5
    }

## Mejoras en progreso

- Coverage con JUnit y Mockito
- DTOs de respuesta para Sala y Funcion
- Seguridad con JWT
- Caché con Redis