package com.tomi.reservas_cine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SalaNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleSalaNoEncontrada(SalaNoEncontradaException e) {
        return e.getMessage();
    }

    @ExceptionHandler(FuncionNoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFuncionNoEncontrada(FuncionNoEncontradaException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AsientoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAsientoNoEncontrado(AsientoNoEncontradoException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AsientoNoDisponibleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAsientoNoDisponible(AsientoNoDisponibleException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        return errores;
    }
}