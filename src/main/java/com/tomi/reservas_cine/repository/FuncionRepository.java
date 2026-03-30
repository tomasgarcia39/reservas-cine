package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
    @Query(value = "SELECT COUNT(*) > 0 FROM funcion WHERE sala_id = :salaId " +
            "AND :horario < horario + (duracion_minutos + 10) * interval '1 minute' " +
            "AND :horario >= horario", nativeQuery = true)
    boolean existsSolapamiento(@Param("salaId") Long salaId,
                               @Param("horario") LocalDateTime horario);
}