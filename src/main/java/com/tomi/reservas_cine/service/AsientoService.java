package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.AsientoResponseDTO;
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

    public List<AsientoResponseDTO> obtenerAsientosPorSala(Long salaId) {
        return asientoRepository.findBySalaId(salaId).stream()
                .map(a -> new AsientoResponseDTO(
                        a.getId(),
                        a.getNumero(),
                        a.getTipo(),
                        a.getEstado().name(),
                        a.getSala().getNombre()))
                .toList();
    }

    public void generarAsientos(Sala sala) {
        for (int i = 1; i <= sala.getCapacidad(); i++) {
            Asiento asiento = new Asiento(i, "ESTANDAR", sala);
            asientoRepository.save(asiento);
        }
    }

    public void eliminarAsientosPorSala(Long salaId) {
        List<Asiento> asientos = asientoRepository.findBySalaId(salaId);
        asientoRepository.deleteAll(asientos);
    }
}