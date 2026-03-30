package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Asiento;
import com.tomi.reservas_cine.model.EstadoAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asiento> findById(Long id);

    List<Asiento> findBySalaId(Long salaId);
    List<Asiento> findByEstadoAndExpiracionBefore(EstadoAsiento estado, LocalDateTime expiracion);
}