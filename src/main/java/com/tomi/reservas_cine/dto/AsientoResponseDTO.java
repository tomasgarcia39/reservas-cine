package com.tomi.reservas_cine.dto;

public record AsientoResponseDTO(
        Long id,
        int numero,
        String tipo,
        String estado,
        String nombreSala
) {}