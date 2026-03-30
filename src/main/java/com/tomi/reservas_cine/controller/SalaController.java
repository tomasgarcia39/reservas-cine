package com.tomi.reservas_cine.controller;
import com.tomi.reservas_cine.dto.SalaRequestDTO;
import com.tomi.reservas_cine.model.Sala;
import com.tomi.reservas_cine.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService){
        this.salaService = salaService;
    }
    @GetMapping
    public  List<Sala>listarSalas(){
        return salaService.obtenerSalas();
    }


    @PostMapping
    public Sala crearSala(@RequestBody @Valid SalaRequestDTO dto) {
        return salaService.guardarSala(new Sala(dto.nombre(), dto.capacidad()));
    }
    @DeleteMapping("/{id}")
    public String eliminarSala(@PathVariable Long id) {
        salaService.eliminarSala(id);
        return "Sala eliminada con exito";
    }
}
