# Sistema de Reservas de Cine

REST API para la gestión de reservas de asientos en un cine, desarrollada como proyecto de aprendizaje de Java backend.

El foco principal es la implementación de **Pessimistic Locking** para garantizar consistencia en reservas concurrentes — si dos usuarios intentan reservar el mismo asiento al mismo tiempo, solo uno lo logra.

## Stack

- Java 21
- Spring Boot 4
- Hibernate / JPA
- PostgreSQL 18
- Docker

## Arquitectura

El proyecto sigue una arquitectura en capas estándar:

    Controller → Service → Repository → PostgreSQL

Con DTOs como filtro entre el Controller y el Service para controlar qué datos entran y salen de la API, y un GlobalExceptionHandler que centraliza el manejo de errores devolviendo respuestas limpias con los códigos HTTP correctos.

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
| POST | `/reservas` | Crear reserva con Pessimistic Lock |

## Ejemplos de uso

**Crear una sala** (genera los asientos automáticamente):

    POST /salas
    {
      "nombre": "Sala 1",
      "capacidad": 100
    }

**Crear una función:**

    POST /funciones?salaId=1&pelicula=Inception&horario=2026-03-28T20:00:00

**Reservar un asiento:**

    POST /reservas
    {
      "nombreUsuario": "tomi",
      "funcionId": 1,
      "asientoId": 5
    }

**Respuesta exitosa:**

    {
      "id": 1,
      "nombreUsuario": "tomi",
      "pelicula": "Inception",
      "horario": "2026-03-28T20:00",
      "numeroAsiento": 5
    }

