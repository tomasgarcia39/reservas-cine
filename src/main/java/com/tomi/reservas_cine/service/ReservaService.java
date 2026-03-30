package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.ReservaRequestDTO;
import com.tomi.reservas_cine.dto.ReservaResponseDTO;
import com.tomi.reservas_cine.exception.*;
import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.model.Reserva;
import com.tomi.reservas_cine.repository.AsientoRepository;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AsientoRepository asientoRepository;
    private final FuncionRepository funcionRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          AsientoRepository asientoRepository,
                          FuncionRepository funcionRepository) {
        this.reservaRepository = reservaRepository;
        this.asientoRepository = asientoRepository;
        this.funcionRepository = funcionRepository;
    }

    @Transactional
    public ReservaResponseDTO reservar(ReservaRequestDTO dto) {
        Funcion funcion = funcionRepository.findById(dto.funcionId())
                .orElseThrow(() -> new AppException(ErrorCode.FUNCION_NO_ENCONTRADA));
        Asiento asiento = asientoRepository.findById(dto.asientoId())
                .orElseThrow(() -> new AppException(ErrorCode.ASIENTO_NO_ENCONTRADO));

        if (!asiento.isDisponible()) {
            throw new AppException(ErrorCode.ASIENTO_NO_DISPONIBLE);
        }

        asiento.setDisponible(false);
        asientoRepository.save(asiento);

        Reserva reserva = new Reserva(dto.nombreUsuario(), funcion, asiento);
        Reserva guardada = reservaRepository.save(reserva);

        return new ReservaResponseDTO(
                guardada.getId(),
                guardada.getNombreUsuario(),
                funcion.getPelicula(),
                funcion.getHorario().toString(),
                asiento.getNumero()
        );
    }

    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }
}