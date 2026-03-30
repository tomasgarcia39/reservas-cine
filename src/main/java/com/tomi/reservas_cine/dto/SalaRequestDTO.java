package com.tomi.reservas_cine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record SalaRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,

        @Positive(message = "La capacidad debe ser mayor a 0")
        int capacidad
) {}