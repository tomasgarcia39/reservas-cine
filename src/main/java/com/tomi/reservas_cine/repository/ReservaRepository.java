package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioEmail(String email);
}