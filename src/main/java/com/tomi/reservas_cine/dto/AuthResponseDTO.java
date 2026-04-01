package com.tomi.reservas_cine.dto;

public record AuthResponseDTO(
        String token,
        String refreshToken,
        String email,
        String rol
) {}