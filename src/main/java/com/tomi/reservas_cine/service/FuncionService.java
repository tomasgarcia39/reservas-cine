package com.tomi.reservas_cine.service;

import com.tomi.reservas_cine.dto.FuncionResponseDTO;
import com.tomi.reservas_cine.exception.AppException;
import com.tomi.reservas_cine.exception.ErrorCode;
import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.repository.FuncionRepository;
import com.tomi.reservas_cine.repository.SalaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FuncionService {

    private final FuncionRepository funcionRepository;
    private final SalaRepository salaRepository;

    public FuncionService(FuncionRepository funcionRepository, SalaRepository salaRepository) {
        this.funcionRepository = funcionRepository;
        this.salaRepository = salaRepository;
    }

    public List<FuncionResponseDTO> obtenerFunciones() {
        return funcionRepository.findAll().stream()
                .map(f -> new FuncionResponseDTO(
                        f.getId(),
                        f.getPelicula(),
                        f.getHorario().toString(),
                        f.getDuracionMinutos(),
                        f.getSala().getNombre()))
                .toList();
    }

    public FuncionResponseDTO crearFuncion(Long salaId, String pelicula, String horario, int duracionMinutos) {
        Sala sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new AppException(ErrorCode.SALA_NO_ENCONTRADA));

        LocalDateTime horarioParseado;
        try {
            horarioParseado = LocalDateTime.parse(horario);
        } catch (DateTimeParseException e) {
            throw new AppException(ErrorCode.HORARIO_INVALIDO);
        }

        if (funcionRepository.existsSolapamiento(salaId, horarioParseado)) {
            throw new AppException(ErrorCode.FUNCION_DUPLICADA);
        }

        Funcion funcion = new Funcion(pelicula, horarioParseado, sala, duracionMinutos);
        Funcion guardada = funcionRepository.save(funcion);
        return new FuncionResponseDTO(
                guardada.getId(),
                guardada.getPelicula(),
                guardada.getHorario().toString(),
                guardada.getDuracionMinutos(),
                guardada.getSala().getNombre());
    }
}