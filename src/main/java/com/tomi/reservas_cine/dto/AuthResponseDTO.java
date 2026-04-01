package com.tomi.reservas_cine.dto;

public record AuthResponseDTO(
        String token,
        String email,
        String rol
) {}