package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.ReservaRequestDTO;
import com.tomi.reservas_cine.dto.ReservaResponseDTO;
import com.tomi.reservas_cine.exception.*;
import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.model.EstadoAsiento;
import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.model.Reserva;
import com.tomi.reservas_cine.model.Usuario;
import com.tomi.reservas_cine.repository.AsientoRepository;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.ReservaRepository;
import com.tomi.reservas_cine.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AsientoRepository asientoRepository;
    private final FuncionRepository funcionRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          AsientoRepository asientoRepository,
                          FuncionRepository funcionRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.asientoRepository = asientoRepository;
        this.funcionRepository = funcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReservaResponseDTO reservar(ReservaRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.CREDENCIALES_INVALIDAS));

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

        Reserva reserva = new Reserva(usuario, funcion, asiento);
        Reserva guardada = reservaRepository.save(reserva);

        return new ReservaResponseDTO(
                guardada.getId(),
                guardada.getUsuario().getEmail(),
                funcion.getPelicula(),
                funcion.getHorario().toString(),
                asiento.getNumero()
        );
    }

    public List<ReservaResponseDTO> obtenerReservas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return reservaRepository.findByUsuarioEmail(email).stream()
                .map(reserva -> new ReservaResponseDTO(
                        reserva.getId(),
                        reserva.getUsuario().getEmail(),
                        reserva.getFuncion().getPelicula(),
                        reserva.getFuncion().getHorario().toString(),
                        reserva.getAsiento().getNumero()
                ))
                .toList();
    }

    @Transactional
    public ReservaResponseDTO confirmarReserva(Long reservaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVA_NO_ENCONTRADA));

        if (!reserva.getUsuario().getEmail().equals(email)) {
            throw new AppException(ErrorCode.ACCESO_DENEGADO);
        }

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
                reserva.getUsuario().getEmail(),
                reserva.getFuncion().getPelicula(),
                reserva.getFuncion().getHorario().toString(),
                asiento.getNumero()
        );
    }
}