package com.tomi.reservas_cine.dto;

public record SalaResponseDTO(
        Long id,
        String nombre,
        int capacidad
) {}