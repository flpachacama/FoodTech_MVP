package com.foodtech.domain.service;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.output.RepartidorRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Servicio de asignación que ofrece métodos para obtener repartidores cercanos.
 */
public class AsignacionService {

    private final RepartidorRepository repartidorRepository;

    public AsignacionService(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = Objects.requireNonNull(repartidorRepository);
    }

    public List<Repartidor> obtenerRepartidoresCercanos(Coordenada restauranteUbicacion) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        if (activos == null || activos.isEmpty()) {
            return Collections.emptyList();
        }

        return activos.stream()
                .sorted((r1, r2) -> Double.compare(
                        r1.getUbicacion().distanciaA(restauranteUbicacion),
                        r2.getUbicacion().distanciaA(restauranteUbicacion)))
                .collect(Collectors.toList());
    }
}
