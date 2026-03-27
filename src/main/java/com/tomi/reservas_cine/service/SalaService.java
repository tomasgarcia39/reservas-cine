package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository){
        this.salaRepository = salaRepository;
    }
    public  List<Sala> obtenerSalas(){
        return salaRepository.findAll();
    }
    public Sala guardarSala(Sala sala){
        return salaRepository.save(sala);
    }
}
