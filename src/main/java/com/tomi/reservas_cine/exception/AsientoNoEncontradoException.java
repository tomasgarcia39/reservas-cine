package com.tomi.reservas_cine.exception;

public class AsientoNoEncontradoException extends RuntimeException {
    public AsientoNoEncontradoException(Long id) {
        super("El asiento " + id + " no existe");
    }
}