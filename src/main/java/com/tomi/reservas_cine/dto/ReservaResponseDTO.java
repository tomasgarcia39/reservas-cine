package com.tomi.reservas_cine.dto;

public record ReservaResponseDTO(
        Long id,
        String nombreUsuario,
        String pelicula,
        String horario,
        int numeroAsiento
) {}