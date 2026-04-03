package com.tomi.reservas_cine.model;

import jakarta.persistence.*;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Funcion funcion;

    @ManyToOne
    private Asiento asiento;

    public Reserva() {}

    public Reserva(Usuario usuario, Funcion funcion, Asiento asiento) {
        this.usuario = usuario;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Funcion getFuncion() { return funcion; }
    public Asiento getAsiento() { return asiento; }
}