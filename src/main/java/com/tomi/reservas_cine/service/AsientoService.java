package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.repository.AsientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsientoService {

    private final AsientoRepository asientoRepository;

    public AsientoService(AsientoRepository asientoRepository) {
        this.asientoRepository = asientoRepository;
    }

    public List<Asiento> obtenerAsientosPorSala(Long salaId) {
        return asientoRepository.findAll().stream()
                .filter(a -> a.getSala().getId().equals(salaId))
                .toList();
    }

    public void generarAsientos(Sala sala) {
        for (int i = 1; i <= sala.getCapacidad(); i++) {
            Asiento asiento = new Asiento(i, "ESTANDAR", sala);
            asientoRepository.save(asiento);
        }
    }

    public void eliminarAsientosPorSala(Long salaId) {
        List<Asiento> asientos = obtenerAsientosPorSala(salaId);
        asientoRepository.deleteAll(asientos);
    }
}