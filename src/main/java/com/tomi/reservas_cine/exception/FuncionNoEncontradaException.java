package com.tomi.reservas_cine.exception;

public class FuncionNoEncontradaException extends RuntimeException {
  public FuncionNoEncontradaException(Long id) {
    super("La funcion " + id + " no existe");
  }
}
