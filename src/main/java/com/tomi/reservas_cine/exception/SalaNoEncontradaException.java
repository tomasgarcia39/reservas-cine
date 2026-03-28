package com.tomi.reservas_cine.exception;

public class SalaNoEncontradaException extends RuntimeException {
    public SalaNoEncontradaException(Long id) {
        super("La sala " + id + " no existe");
    }
}