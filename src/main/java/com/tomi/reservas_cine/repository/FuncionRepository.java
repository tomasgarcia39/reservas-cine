package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    boolean existsBySalaIdAndHorario(Long salaId, LocalDateTime horario);
}