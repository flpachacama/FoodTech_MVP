package com.foodtech.application.service;

import com.foodtech.domain.model.Clima;
import com.foodtech.domain.model.Coordenada;
import com.foodtech.domain.model.EstadoRepartidor;
import com.foodtech.domain.model.Repartidor;
import com.foodtech.domain.port.input.AsignacionUseCase;
import com.foodtech.domain.port.output.RepartidorRepository;
import com.foodtech.domain.service.AsignacionService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AsignacionApplicationService implements AsignacionUseCase {

    private final RepartidorRepository repartidorRepository;
    private final AsignacionService asignacionService;

    public AsignacionApplicationService(RepartidorRepository repartidorRepository,
                                       AsignacionService asignacionService) {
        this.repartidorRepository = Objects.requireNonNull(repartidorRepository);
        this.asignacionService = Objects.requireNonNull(asignacionService);
    }

    @Override
    public List<Repartidor> obtenerRepartidoresPriorizados(Coordenada restauranteUbicacion, Clima clima) {
        List<Repartidor> activos = repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO);
        
        if (activos == null || activos.isEmpty()) {
            return Collections.emptyList();
        }
        return asignacionService.priorizarRepartidores(activos, restauranteUbicacion, clima);
    }
}
