package com.tomi.reservas_cine.job;

import com.tomi.reservas_cine.model.EstadoAsiento;
import com.tomi.reservas_cine.model.EstadoAsiento;
import com.tomi.reservas_cine.repository.AsientoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AsientoExpirationJob {

    private final AsientoRepository asientoRepository;

    public AsientoExpirationJob(AsientoRepository asientoRepository) {
        this.asientoRepository = asientoRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void liberarAsientosExpirados() {
        asientoRepository.findByEstadoAndExpiracionBefore(
                EstadoAsiento.RESERVADO_TEMP,
                LocalDateTime.now()
        ).forEach(asiento -> {
            asiento.setEstado(EstadoAsiento.DISPONIBLE);
            asiento.setExpiracion(null);
            asientoRepository.save(asiento);
        });
    }
}