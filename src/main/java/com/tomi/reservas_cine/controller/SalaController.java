package com.tomi.reservas_cine.controller;

import com.tomi.reservas_cine.dto.SalaRequestDTO;
import com.tomi.reservas_cine.dto.SalaResponseDTO;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public List<SalaResponseDTO> listarSalas() {
        return salaService.obtenerSalas();
    }

    @PostMapping
    public SalaResponseDTO crearSala(@RequestBody @Valid SalaRequestDTO dto) {
        return salaService.guardarSala(new Sala(dto.nombre(), dto.capacidad()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarSala(@PathVariable Long id) {
        salaService.eliminarSala(id);
    }
}