package com.tomi.reservas_cine.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pelicula;
    private LocalDateTime horario;

    @ManyToOne
    private Sala sala;

    public Funcion() {}

    public Funcion(String pelicula, LocalDateTime horario, Sala sala) {
        this.pelicula = pelicula;
        this.horario = horario;
        this.sala = sala;
    }

    public Long getId() { return id; }
    public String getPelicula() { return pelicula; }
    public LocalDateTime getHorario() { return horario; }
    public Sala getSala() { return sala; }
}