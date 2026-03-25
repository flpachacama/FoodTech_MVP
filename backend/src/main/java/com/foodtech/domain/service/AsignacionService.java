package com.foodtech.domain.service;

import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.model.TipoVehiculo;
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

    public List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        if (activos == null || activos.isEmpty()) {
            return Collections.emptyList();
        }

        return activos.stream()
            .filter(r -> esVehiculoApto(r.getVehiculo(), clima))
            .sorted((r1, r2) -> {
                double d1 = r1.getUbicacion().distanciaA(restauranteUbicacion);
                double d2 = r2.getUbicacion().distanciaA(restauranteUbicacion);
                double t1 = calcularTiempoEstimado(d1, r1.getVehiculo().getVelocidadKmH());
                double t2 = calcularTiempoEstimado(d2, r2.getVehiculo().getVelocidadKmH());
                return Double.compare(t1, t2);
            })
            .collect(Collectors.toList());
    }

    private double calcularTiempoEstimado(double distancia, int velocidad) {
        return distancia / velocidad;
    }

    private boolean esVehiculoApto(TipoVehiculo vehiculo, Clima clima) {
        if (clima == null) {
            return true;
        }
        return switch (clima) {
            case LLUVIA_FUERTE -> vehiculo == TipoVehiculo.AUTO;
            case LLUVIA_SUAVE -> vehiculo == TipoVehiculo.MOTO || vehiculo == TipoVehiculo.AUTO;
            case SOLEADO -> true;
        };
    }
}
