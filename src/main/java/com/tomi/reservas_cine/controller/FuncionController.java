package com.tomi.reservas_cine.controller;

import com.tomi.reservas_cine.dto.FuncionRequestDTO;
import com.tomi.reservas_cine.dto.FuncionResponseDTO;
import com.tomi.reservas_cine.service.FuncionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funciones")
public class FuncionController {

    private final FuncionService funcionService;

    public FuncionController(FuncionService funcionService) {
        this.funcionService = funcionService;
    }

    @GetMapping
    public List<FuncionResponseDTO> listarFunciones() {
        return funcionService.obtenerFunciones();
    }

    @PostMapping
    public FuncionResponseDTO crearFuncion(@RequestBody @Valid FuncionRequestDTO dto) {
        return funcionService.crearFuncion(dto.salaId(), dto.pelicula(), dto.horario(), dto.duracionMinutos());
    }
}