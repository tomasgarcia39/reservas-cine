package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.exception.AppException;
import com.tomi.reservas_cine.exception.ErrorCode;
import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.SalaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final SalaRepository salaRepository;

    public FuncionService(FuncionRepository funcionRepository, SalaRepository salaRepository) {
        this.funcionRepository = funcionRepository;
        this.salaRepository = salaRepository;
    }

    public List<Funcion> obtenerFunciones() {
        return funcionRepository.findAll();
    }

    public Funcion crearFuncion(Long salaId, String pelicula, String horario) {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new AppException(ErrorCode.SALA_NO_ENCONTRADA));
        Funcion funcion = new Funcion(pelicula, java.time.LocalDateTime.parse(horario), sala);
        return funcionRepository.save(funcion);
    }
}