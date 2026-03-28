package com.tomi.reservas_cine.exception;

public class AsientoNoDisponibleException extends RuntimeException {
    public AsientoNoDisponibleException(Long id) {
        super("El asiento " + id + " ya esta reservado");
    }
}