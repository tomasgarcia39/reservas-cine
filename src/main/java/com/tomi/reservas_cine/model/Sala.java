package com.tomi.reservas_cine.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private int capacidad;

    public Sala(){}

    public Sala(String nombre, int capacidad){
        this.nombre = nombre;
        this.capacidad = capacidad;
    }
    public Long getrId(){ return id ;}
    public String getNombre(){return nombre;}
    public int getCapacidad(){ return capacidad;}


}
