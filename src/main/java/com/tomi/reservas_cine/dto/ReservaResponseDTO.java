package com.tomi.reservas_cine.dto;

public class ReservaResponseDTO {

    private Long id;
    private String nombreUsuario;
    private String pelicula;
    private String horario;
    private int numeroAsiento;

    public ReservaResponseDTO(Long id, String nombreUsuario, String pelicula, String horario, int numeroAsiento) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.pelicula = pelicula;
        this.horario = horario;
        this.numeroAsiento = numeroAsiento;
    }

    public Long getId() { return id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getPelicula() { return pelicula; }
    public String getHorario() { return horario; }
    public int getNumeroAsiento() { return numeroAsiento; }
}