package com.tomi.reservas_cine.controller;

import com.tomi.reservas_cine.model.Funcion;
import com.tomi.reservas_cine.service.FuncionService;
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
    public List<Funcion> listarFunciones() {
        return funcionService.obtenerFunciones();
    }

    @PostMapping
    public Funcion crearFuncion(@RequestParam Long salaId,
                                @RequestParam String pelicula,
                                @RequestParam String horario,
                                @RequestParam int duracionMinutos) {
        return funcionService.crearFuncion(salaId, pelicula, horario, duracionMinutos);
    }

}