package com.tomi.reservas_cine.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SALA_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La sala no existe"),
    FUNCION_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La funcion no existe"),
    ASIENTO_NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El asiento no existe"),
    ASIENTO_NO_DISPONIBLE(HttpStatus.CONFLICT, "El asiento ya esta reservado"),
    FUNCION_DUPLICADA(HttpStatus.CONFLICT, "Ya existe una funcion en esa sala a ese horario"),
    ASIENTO_NO_DISPONIBLE_TEMP(HttpStatus.CONFLICT, "El asiento esta siendo reservado por otro usuario"),
    RESERVA_NO_ENCONTRADA(HttpStatus.NOT_FOUND, "La reserva no existe"),
    HORARIO_INVALIDO(HttpStatus.BAD_REQUEST, "Formato de horario inválido. Use: yyyy-MM-ddTHH:mm:ss"),
    EMAIL_YA_REGISTRADO(HttpStatus.CONFLICT, "El email ya está registrado"),
    CREDENCIALES_INVALIDAS(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos"),
    TOKEN_INVALIDO(HttpStatus.UNAUTHORIZED, "El token es inválido o expiró"),
    ACCESO_DENEGADO(HttpStatus.FORBIDDEN, "No tenés permiso para realizar esta acción");
    private final HttpStatus status;
    private final String mensaje;

    ErrorCode(HttpStatus status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    public HttpStatus getStatus() { return status; }
    public String getMensaje() { return mensaje; }
}