package com.tomi.reservas_cine.model;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;
    private String tipo;
    private EstadoAsiento estado;
    private LocalDateTime expiracion;
    @ManyToOne
    private Sala sala;

    public Asiento(){}

    public Asiento(int numero, String tipo, Sala sala){
        this.numero = numero;
        this. tipo= tipo;
        this.estado = EstadoAsiento.DISPONIBLE;
        this.sala = sala;
    }
public Long getId(){return id;}
public int getNumero(){return numero;}
public String getTipo(){return tipo;}
public Sala getSala(){return sala;}
    public EstadoAsiento getEstado(){return estado;}
    public LocalDateTime getExpiracion(){return expiracion;}
    public void setEstado(EstadoAsiento estado){this.estado = estado;}
    public void setExpiracion(LocalDateTime expiracion){this.expiracion = expiracion;}


}
