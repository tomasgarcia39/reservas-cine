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

        if (funcionRepository.existsSolapamiento(salaId, LocalDateTime.parse(horario))) {
            throw new AppException(ErrorCode.FUNCION_DUPLICADA);
        }

        Funcion funcion = new Funcion(pelicula, LocalDateTime.parse(horario), sala, duracionMinutos);
        Funcion guardada = funcionRepository.save(funcion);
        return new FuncionResponseDTO(
                guardada.getId(),
                guardada.getPelicula(),
                guardada.getHorario().toString(),
                guardada.getDuracionMinutos(),
                guardada.getSala().getNombre());
    }
    }
