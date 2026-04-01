package com.tomi.reservas_cine.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDTO(
        @NotBlank
        String refreshToken
) {}