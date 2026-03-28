package com.tomi.reservas_cine.model;

import jakarta.persistence.*;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreUsuario;

    @ManyToOne
    private Funcion funcion;

    @ManyToOne
    private Asiento asiento;

    public Reserva() {}

    public Reserva(String nombreUsuario, Funcion funcion, Asiento asiento) {
        this.nombreUsuario = nombreUsuario;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    public Long getId() { return id; }
    public String getNombreUsuario() { return nombreUsuario; }
    public Funcion getFuncion() { return funcion; }
    public Asiento getAsiento() { return asiento; }
}