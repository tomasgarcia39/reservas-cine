package com.tomi.reservas_cine.model;
import jakarta.persistence.*;

@Entity
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;
    private String tipo;
    private boolean disponible;

    @ManyToOne
    private Sala sala;

    public Asiento(){}

    public Asiento(int numero, String tipo, Sala sala){
        this.numero = numero;
        this. tipo= tipo;
        this.disponible = true;
        this.sala = sala;
    }
public Long getId(){return id;}
public int getNumero(){return numero;}
public String getTipo(){return tipo;}
public boolean isDisponible(){return disponible;}
public Sala getSala(){return sala;}
public void setDisponible(boolean disponible){this.disponible = disponible;}

}
