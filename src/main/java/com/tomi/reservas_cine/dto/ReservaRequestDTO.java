package com.tomi.reservas_cine.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservaRequestDTO(
        @NotNull(message = "La función es obligatoria")
        @Positive(message = "El id de la función debe ser mayor a 0")
        Long funcionId,

        @NotNull(message = "El asiento es obligatorio")
        @Positive(message = "El id del asiento debe ser mayor a 0")
        Long asientoId
) {}