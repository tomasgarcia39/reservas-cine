package com.tomi.reservas_cine.controller;

import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.service.AsientoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asientos")
public class AsientoController {

    private final AsientoService asientoService;

    public AsientoController(AsientoService asientoService) {
        this.asientoService = asientoService;
    }

    @GetMapping("/sala/{salaId}")
    public List<Asiento> listarAsientosPorSala(@PathVariable Long salaId) {
        return asientoService.obtenerAsientosPorSala(salaId);
    }
}