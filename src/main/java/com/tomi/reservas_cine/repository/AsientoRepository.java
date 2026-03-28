package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsientoRepository extends JpaRepository<Asiento, Long> {
}