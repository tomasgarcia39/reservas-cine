package com.tomi.reservas_cine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservaRequestDTO {

    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    private String nombreUsuario;

    @NotNull(message = "La funcion es obligatoria")
    private Long funcionId;

    @NotNull(message = "El asiento es obligatorio")
    private Long asientoId;

    public String getNombreUsuario() { return nombreUsuario; }
    public Long getFuncionId() { return funcionId; }
    public Long getAsientoId() { return asientoId; }
}