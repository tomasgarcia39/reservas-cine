package com.tomi.reservas_cine.controller;

import com.tomi.reservas_cine.dto.ReservaRequestDTO;
import com.tomi.reservas_cine.dto.ReservaResponseDTO;
import com.tomi.reservas_cine.model.Reserva;
import com.tomi.reservas_cine.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listarReservas() {
        return reservaService.obtenerReservas();
    }

    @PostMapping
    public ReservaResponseDTO reservar(@Valid @RequestBody ReservaRequestDTO dto) {
        return reservaService.reservar(dto);
    }
    @PostMapping("/{id}/confirmar")
    public ReservaResponseDTO confirmar(@PathVariable Long id) {
        return reservaService.confirmarReserva(id);
    }
}