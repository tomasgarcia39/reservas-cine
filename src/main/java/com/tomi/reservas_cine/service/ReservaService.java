package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.ReservaRequestDTO;
import com.tomi.reservas_cine.dto.ReservaResponseDTO;
import com.tomi.reservas_cine.exception.*;
import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.model.EstadoAsiento;
import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.model.Reserva;
import com.tomi.reservas_cine.repository.AsientoRepository;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

        if (asiento.getEstado() == EstadoAsiento.CONFIRMADO) {
            throw new AppException(ErrorCode.ASIENTO_NO_DISPONIBLE);
        }

        if (asiento.getEstado() == EstadoAsiento.RESERVADO_TEMP &&
                asiento.getExpiracion() != null &&
                asiento.getExpiracion().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.ASIENTO_NO_DISPONIBLE_TEMP);
        }

        asiento.setEstado(EstadoAsiento.RESERVADO_TEMP);
        asiento.setExpiracion(LocalDateTime.now().plusMinutes(10));
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

    public List<ReservaResponseDTO> obtenerReservas() {
        return reservaRepository.findAll().stream()
                .map(reserva -> new ReservaResponseDTO(
                        reserva.getId(),
                        reserva.getNombreUsuario(),
                        reserva.getFuncion().getPelicula(),
                        reserva.getFuncion().getHorario().toString(),
                        reserva.getAsiento().getNumero()
                ))
                .toList();
    }
    @Transactional
    public ReservaResponseDTO confirmarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVA_NO_ENCONTRADA));

        Asiento asiento = reserva.getAsiento();

        if (asiento.getEstado() != EstadoAsiento.RESERVADO_TEMP) {
            throw new AppException(ErrorCode.ASIENTO_NO_DISPONIBLE);
        }

        if (asiento.getExpiracion().isBefore(LocalDateTime.now())) {
            asiento.setEstado(EstadoAsiento.DISPONIBLE);
            asiento.setExpiracion(null);
            asientoRepository.save(asiento);
            throw new AppException(ErrorCode.ASIENTO_NO_DISPONIBLE_TEMP);
        }

        asiento.setEstado(EstadoAsiento.CONFIRMADO);
        asiento.setExpiracion(null);
        asientoRepository.save(asiento);

        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getNombreUsuario(),
                reserva.getFuncion().getPelicula(),
                reserva.getFuncion().getHorario().toString(),
                asiento.getNumero()
        );
    }
}