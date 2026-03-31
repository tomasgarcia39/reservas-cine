package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.SalaResponseDTO;
import com.tomi.reservas_cine.exception.AppException;
import com.tomi.reservas_cine.exception.ErrorCode;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final AsientoService asientoService;

    public SalaService(SalaRepository salaRepository, AsientoService asientoService) {
        this.salaRepository = salaRepository;
        this.asientoService = asientoService;
    }

    public List<SalaResponseDTO> obtenerSalas() {
        return salaRepository.findAll().stream()
                .map(sala -> new SalaResponseDTO(sala.getId(), sala.getNombre(), sala.getCapacidad()))
                .toList();
    }

    public SalaResponseDTO guardarSala(Sala sala) {
        Sala salaguardada = salaRepository.save(sala);
        asientoService.generarAsientos(salaguardada);
        return new SalaResponseDTO(salaguardada.getId(), salaguardada.getNombre(), salaguardada.getCapacidad());
    }

    public void eliminarSala(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new AppException(ErrorCode.SALA_NO_ENCONTRADA);
        }
        asientoService.eliminarAsientosPorSala(id);
        salaRepository.deleteById(id);
    }
}