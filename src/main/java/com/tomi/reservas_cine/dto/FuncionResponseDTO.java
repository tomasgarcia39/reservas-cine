package com.tomi.reservas_cine.dto;

public record FuncionResponseDTO(
        Long id,
        String pelicula,
        String horario,
        int duracionMinutos,
        String nombreSala
) {}