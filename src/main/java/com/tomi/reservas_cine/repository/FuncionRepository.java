package com.tomi.reservas_cine.repository;

import com.tomi.reservas_cine.model.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionRepository extends JpaRepository<Funcion, Long> {
}