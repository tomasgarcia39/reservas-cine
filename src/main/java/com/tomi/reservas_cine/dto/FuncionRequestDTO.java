package com.tomi.reservas_cine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FuncionRequestDTO(
        @NotNull(message = "La sala es obligatoria")
        @Positive(message = "El id de la sala debe ser mayor a 0")
        Long salaId,

        @NotBlank(message = "La pelicula no puede estar vacía")
        String pelicula,

        @NotBlank(message = "El horario no puede estar vacío")
        String horario,

        @Positive(message = "La duración debe ser mayor a 0")
        int duracionMinutos
) {}